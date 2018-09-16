//
//  MapViewController.swift
//  whereTo
//
//  Created by Krrish Dholakia on 9/15/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import UIKit
import MapKit
class MapViewController: UIViewController {
    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var placeName: UILabel!
    var name: String = ""
    var latitude: String = ""
    var longitude: String = ""
    @IBAction func goTogetherBtn(_ sender: Any) {
    }
    @IBAction func goBtn(_ sender: Any) {
        if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
            UIApplication.shared.openURL(URL(string:
                "comgooglemaps://?center=\(latitude),\(longitude)&zoom=14&views=traffic")!)
        } else {
            print("Can't use comgooglemaps://");
        }

    }
    
    @IBAction func reRollBtn(_ sender: Any) {
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        placeName.text = name
        print("latitude " + latitude)
        print("longitude " + longitude)
        mapView.centerCoordinate = CLLocationCoordinate2D(latitude: Double(latitude)!, longitude: Double(longitude)!)
        let myAnnotation: MKPointAnnotation = MKPointAnnotation()
        myAnnotation.coordinate = CLLocationCoordinate2DMake(Double(latitude)!, Double(longitude)!)
        myAnnotation.title = name
        mapView.addAnnotation(myAnnotation)
        mapView.region.span.latitudeDelta = 0.2;
        mapView.region.span.longitudeDelta = 0.2;
        
        // Do any additional setup after loading the view.
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
