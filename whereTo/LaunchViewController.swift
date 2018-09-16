//
//  LaunchViewController.swift
//  whereTo
//
//  Created by Krrish Dholakia on 9/15/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import UIKit
import Alamofire
import MapKit
import SwiftyJSON


class LaunchViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, CLLocationManagerDelegate{
    @IBOutlet weak var actionPicker: UIPickerView!
    var currentPickerField = UITextView()
    @IBOutlet weak var distancePicker: UIPickerView!
    
    let actions = ["places to go", "things to do", "stuff to eat"]
    let distances = ["walk", "drive", "travel"]
    
    var actionTxt: String = ""
    var distanceTxt: String = ""
    
    var locationname: String = ""
    var locationlatitude: String = ""
    var locationlongitude: String = ""
    private var locationManager: CLLocationManager!
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    override func viewDidLoad() {
        super.viewDidLoad()
        actionPicker?.delegate = self
        actionPicker?.dataSource = self
        
        distancePicker?.delegate = self
        distancePicker?.dataSource = self
        
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        
        if CLLocationManager.locationServicesEnabled() {
            locationManager.requestWhenInUseAuthorization()
            locationManager.startUpdatingLocation()
        }
        // Do any additional setup after loading the view.
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
            return distances.count
        } else {
            return 0
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if pickerView == actionPicker {
            return actions[row]
        } else if pickerView == distancePicker {
            return distances[row]
        } else {
            return ""
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerView == actionPicker {
            actionTxt = actions[row]
        } else if pickerView == distancePicker {
            distanceTxt = distances[row]
        }
    }
    @IBAction func goBtn(_ sender: Any) {
//        Alamofire.request("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=\(latitude),\(longitude)&radius=500&type=restaurant&key=AIzaSyBfiWNwy-JuUD59MQqEa_PkEiIlmVmVSu0", method: HTTPMethod.get, encoding: JSONEncoding.default, headers: nil).validate().responseString {
//            response in
//            print(response)
//            let JSON = response as! NSDictionary
//            var json = JSON(JSON)
//            print (json.count)
//        }
        
        Alamofire.request("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=\(latitude),\(longitude)&radius=500&type=restaurant&key=AIzaSyBfiWNwy-JuUD59MQqEa_PkEiIlmVmVSu0", method: HTTPMethod.get, encoding: JSONEncoding.default, headers: nil).responseJSON {
            response in
            if let jsonValue = response.result.value {
                let json = SwiftyJSON.JSON(jsonValue)
                print(json["results"])
                var counter: Int = 0
                print(json["results"].count)
                let randnum = Int(arc4random_uniform(UInt32(json["results"].count)))
                print(json["results"][randnum]["name"])
                self.locationname = json["results"][randnum]["name"].stringValue
                self.locationlatitude = json["results"][randnum]["geometry"]["location"]["lat"].stringValue
                self.locationlongitude = json["results"][randnum]["name"]["location"]["lng"].stringValue
                self.performSegue(withIdentifier: "mapSegue", sender: self)
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? MapViewController {
            destination.name = self.locationname
            destination.latitude = self.locationlatitude
            destination.longitude = self.locationlongitude
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
