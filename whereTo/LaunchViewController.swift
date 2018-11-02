//
//  LaunchViewController.swift
//  whereTo
//
//  Created by Krrish Dholakia on 9/15/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import UIKit
// import Alamofire
import MapKit
import SwiftyJSON
import Pulsator
import JJFloatingActionButton

class LaunchViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, CLLocationManagerDelegate{
    @IBOutlet weak var actionPicker: UIPickerView!
    var currentPickerField = UITextView()
    @IBOutlet weak var distancePicker: UIPickerView!
    
    let actions = ["places to go", "things to do", "stuff to eat"]
    let travelmodes = ["walk", "drive", "travel"]
    let realtravelmodes = ["walking", "driving", "transit"]
    var realtravel: String = "walking"
    let distances = [1500, 26093, 48280]
    var distance: Int = 1000
    let placesToGo = "amusement_park,aquarium,art_gallery,natural_feature,cafe,casino,library,hindu_temple,museum,park,stadium,zoo"
    let thingsToDo = "bowling_alley,bookstore,gym,shopping_mall,spa,movie_theater,movie_rental"
    let stuffToEat = "bakery,bar,cafe,food,restaurant"
    var action: String = ""
    @IBOutlet weak var testLaunch: UIImageView!
    
    @IBOutlet var parentView: UIView!
    var actionTxt: String = "places to go"
    var distanceTxt: String = "walk"
    
    var locationname: String = ""
    var locationlatitude: String = ""
    var locationlongitude: String = ""
    private var locationManager: CLLocationManager!
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    
    let leftActionButton = JJFloatingActionButton()
    let rightActionButton = JJFloatingActionButton()
    let pulsator = Pulsator()

    override func viewWillAppear(_ animated: Bool) {
        pulsator.radius = 200.0
        parentView.layer.insertSublayer(pulsator, below: testLaunch.layer)
        pulsator.position = testLaunch.center
        pulsator.numPulse = 3
        pulsator.start()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        pulsator.position = testLaunch.layer.position
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        actionPicker?.delegate = self
        actionPicker?.dataSource = self
        
        distancePicker?.delegate = self
        distancePicker?.dataSource = self
        
        self.view.addSubview(leftActionButton)
        self.view.addSubview(rightActionButton)
        
        testLaunch.frame = CGRect(x: parentView.center.x, y: testLaunch.frame.midY, width: testLaunch.frame.width, height: testLaunch.frame.height)
        
        leftActionButton.translatesAutoresizingMaskIntoConstraints = false
        leftActionButton.widthAnchor.constraint(equalToConstant: 65).isActive = true
        leftActionButton.heightAnchor.constraint(equalToConstant: 65).isActive = true
        leftActionButton.configureDefaultItem { item in
            item.titlePosition = .right
        }
        if #available(iOS 11.0, *) {
            leftActionButton.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor, constant: 16).isActive = true
            leftActionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16).isActive = true
        } else {
            leftActionButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 16).isActive = true
            leftActionButton.bottomAnchor.constraint(equalTo: bottomLayoutGuide.topAnchor, constant: -16).isActive = true
        }
        leftActionButton.addItem(title: "Walk", image: UIImage(named: "walking")?.withRenderingMode(.alwaysTemplate)) { item in
            self.leftActionButton.buttonImage = item.buttonImage
        }
        
        leftActionButton.addItem(title: "Drive", image: UIImage(named: "car")?.withRenderingMode(.alwaysTemplate)) { item in
            self.leftActionButton.buttonImage = item.buttonImage
        }
        
        leftActionButton.addItem(title: "Transit", image: UIImage(named: "subway")?.withRenderingMode(.alwaysTemplate)) { item in
            self.leftActionButton.buttonImage = item.buttonImage
        }
        
        
        rightActionButton.translatesAutoresizingMaskIntoConstraints = false
        rightActionButton.widthAnchor.constraint(equalToConstant: 65).isActive = true
        rightActionButton.heightAnchor.constraint(equalToConstant: 65).isActive = true
        rightActionButton.configureDefaultItem { item in
            item.titlePosition = .left
        }
        rightActionButton.display(inViewController: self)
        rightActionButton.addItem(title: "Places to go", image: UIImage(named: "map")?.withRenderingMode(.alwaysTemplate)) { item in
            self.rightActionButton.buttonImage = item.buttonImage
        }
        
        rightActionButton.addItem(title: "Stuff to do", image: UIImage(named: "theme-park")?.withRenderingMode(.alwaysTemplate)) { item in
            self.rightActionButton.buttonImage = item.buttonImage
        }
        
        rightActionButton.addItem(title: "Things to eat", image: UIImage(named: "dining")?.withRenderingMode(.alwaysTemplate)) { item in
            self.rightActionButton.buttonImage = item.buttonImage
        }
        
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.requestWhenInUseAuthorization()
            locationManager.startUpdatingLocation()
        }
        
        let singleTap = UITapGestureRecognizer(target: self, action: #selector(self.testLaunchClicked))
        testLaunch.isUserInteractionEnabled = true
        testLaunch.addGestureRecognizer(singleTap)

    }
    
    @objc func testLaunchClicked() {
        print("action " + action)
//        Alamofire.request("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=\(latitude),\(longitude)&radius=\(distance)&type=\(action)&key=AIzaSyBfiWNwy-JuUD59MQqEa_PkEiIlmVmVSu0", method: HTTPMethod.get, encoding: JSONEncoding.default, headers: nil).responseJSON {
//            response in
//            if let jsonValue = response.result.value {
//                let json = SwiftyJSON.JSON(jsonValue)
//                print(json["results"])
//                print(json["results"].count)
//                let randnum = Int(arc4random_uniform(UInt32(json["results"].count)))
//                print(json["results"][randnum]["name"])
//                self.locationname = json["results"][randnum]["name"].stringValue
//                self.locationlatitude = json["results"][randnum]["geometry"]["location"]["lat"].stringValue
//                self.locationlongitude = json["results"][randnum]["geometry"]["location"]["lng"].stringValue
//                self.performSegue(withIdentifier: "mapSegue", sender: self)
//            }
//        }
    }
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let locValue: CLLocationCoordinate2D = manager.location?.coordinate else { return }
        latitude = locValue.latitude
        longitude = locValue.longitude
        print("locations = \(latitude) \(longitude)")
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1;
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if pickerView == actionPicker {
            return actions.count
        } else if pickerView == distancePicker {
            return travelmodes.count
        } else {
            return 0
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if pickerView == actionPicker {
            return actions[row]
        } else if pickerView == distancePicker {
            return travelmodes[row]
        } else {
            return ""
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerView == actionPicker {
            actionTxt = actions[row]
            if (actionTxt == "places to go") {
                action = placesToGo
            } else if (actionTxt == "things to do") {
                action = thingsToDo
            } else if (actionTxt == "stuff to eat") {
                action = stuffToEat
            }
        } else if pickerView == distancePicker {
            distanceTxt = travelmodes[row]
            distance = distances[row]
            realtravel = realtravelmodes[row]
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? MapViewController {
            destination.name = self.locationname
            destination.dlatitude = self.locationlatitude
            destination.dlongitude = self.locationlongitude
            destination.slatitude = String(self.latitude)
            destination.slongitude = String(self.longitude)
            destination.travelMode = realtravel
        }
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
