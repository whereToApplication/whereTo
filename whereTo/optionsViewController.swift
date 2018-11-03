//
//  optionsViewController.swift
//  whereTo
//
//  Created by Nirmit Shah on 10/20/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import Foundation
import UIKit
import MapKit
import Alamofire
import SwiftyJSON
class optionsViewController: UIViewController, CLLocationManagerDelegate {
    var timeText: String = ""
    var radiusText: String = ""
    var eventsText: String = ""
    var travelTypeText: String = ""
    var paceText: String = ""
    let distances = [1500, 26093, 48280]
    var distance: Int = 1000
    private var locationManager: CLLocationManager!
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var k = 4
    var delta = 0
    let sigma = 0.4
    var spotList: [Place] = []
    var masterList: [Place] = []
    var done: Bool = false
    var options: [JSON] = []
    var updated: Bool = false;
    @IBAction func timeAction(_ sender: UIDatePicker) {
        let date = sender
        
        let calendar = Calendar.current
        let comp = calendar.dateComponents([.hour, .minute], from: date.date)
        let hour = comp.hour
        let minute = comp.minute
        let currDate = Date()
        let currCalendar = Calendar.current
        let currHour = calendar.component(.hour, from: currDate)
        let currMinutes = calendar.component(.minute, from: currDate)
        
        timeText = String((60*hour! + minute!) - (60*currHour + currMinutes))
        print(timeText)
    }
    @IBAction func eventAction(_ sender: UISegmentedControl) {
        eventsText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
    }
    @IBAction func radiusAction(_ sender: UISegmentedControl) {
        radiusText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
    }
    
    @IBAction func travelAction(_ sender: UISegmentedControl) {
            travelTypeText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
        if travelTypeText == "Walk" {
            distance = distances[0]
        } else if travelTypeText == "Drive" {
            distance = distances[1]
        } else if travelTypeText == "Transit" || travelTypeText == "Any" {
            distance = distances[2]
        }
    }
    @IBAction func paceAction(_ sender: UISegmentedControl) {
        paceText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
        k = kCalculator()
    }


   

    @IBAction func submit(_ sender: UIButton) {
        if radiusText.count > 0 && eventsText.count > 0 && travelTypeText.count > 0 && paceText.count > 0 {
            performSegue(withIdentifier: "senddata", sender: self)
        } else {
            let alert = UIAlertController(title: "My Alert", message: "This is an alert.", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Be sure to pick everything"), style: .default, handler: { _ in
                NSLog("The \"OK\" alert occured.")
            }))
            self.present(alert, animated: true, completion: nil)
        }
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? AlgorithmController {
            destination.radius = self.radiusText
            destination.event = self.eventsText
            destination.travel = self.travelTypeText
            destination.place = self.paceText
            destination.options1 = self.spotList
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.requestWhenInUseAuthorization()
            locationManager.startUpdatingLocation()
        }
        
        
        
        delta = Int.random(in: 0...k)
        
    }
    
    func buildSpotList() {
        for i in 0...delta {
            if Double.random(in: 0 ... 1) < sigma {
                swap(&spotList[i], &masterList[Int.random(in: 0...masterList.count-k)])
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let locValue: CLLocationCoordinate2D = manager.location?.coordinate else { return }
        latitude = locValue.latitude
        longitude = locValue.longitude
        if !updated {
            testLaunchClicked();
            updated = true;
        }

//        print("locations = \(latitude) \(longitude)")
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        }
    
    
    @objc func testLaunchClicked() {
        
        
        let auth_header = [
            "Authorization": "Bearer IRT-fzwU1f8luW7wcdWJ5wSzmTOWoJuYKAOMZJtlv-D6s-MVhzGwu7MLn77_A2NWUohglYO_WZhBgejDmHINDKSSP-jzSKFoa_DeL3TdYGrezK1TFeYaHLagsmvLW3Yx",
            ]

        
        Alamofire.request("https://api.yelp.com/v3/businesses/search?term=arts&latitude=\(self.latitude)&longitude=\(self.longitude)&radius=20000", method: HTTPMethod.get, parameters: nil, encoding: JSONEncoding.default, headers: auth_header).responseJSON {
            response in
            if let jsonValue = response.result.value {
                let json = SwiftyJSON.JSON(jsonValue)
                self.options = json["businesses"].arrayValue
                self.options.sort { $0["rating"].doubleValue > $1["rating"].doubleValue }
                var newPlaces: [Place] = []
                let userLocation: coordinates = coordinates(latitude: self.latitude, longitude: self.longitude)
                let userPlace: Place = Place(name: "Your location", coordinates: userLocation, rating: 0, review_count: 0)
                newPlaces.append(userPlace);
                for count in 0 ... self.options.count - 1 {
                    let tempcoord: coordinates = coordinates(latitude: self.options[count]["coordinates"]["latitude"].doubleValue, longitude: self.options[count]["coordinates"]["longitude"].doubleValue);
                    let tempPlace: Place = Place(name: self.options[count]["name"].stringValue, coordinates: tempcoord, rating: self.options[count]["rating"].intValue, review_count: self.options[count]["review_count"].intValue);
                    
                    newPlaces.append(tempPlace);
                    
                }
                
                if self.k > newPlaces.count {
                    self.spotList = newPlaces;
                    self.done = true
                } else {
                    for count in 0 ... self.k {
                        self.spotList.append(newPlaces[count]);
                    }
                    self.masterList = newPlaces
                    
                }
                
                self.performSegue(withIdentifier: "algorithmIdentifier", sender: self);
            }
        }
    }
    
    
    func kCalculator() -> Int {
        if paceText == "Chill" {
            return Int(timeText)!/90
        } else if paceText == "Moderate" {
            return Int(timeText)!/45
        } else if paceText == "Fast" {
            return Int(timeText)!/15
        }
        
        return 0
    }
}
