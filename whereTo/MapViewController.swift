//
//  MapViewController.swift
//  whereTo
//
//  Created by Krrish Dholakia on 9/15/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import UIKit
import MapKit
class MapViewController: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var placeName: UILabel!
    var name: String = ""
    var dlatitude: String = ""
    var dlongitude: String = ""
    var slatitude: String = ""
    var slongitude: String = ""
    var travelMode: String = ""
    @IBOutlet weak var goTogetherLabel: UIImageView!
    @IBOutlet weak var goLabel: UIImageView!
    @IBOutlet weak var reRollLabel: UIImageView!
    
    // set initial location in Honolulu
    var initialLocation : CLLocation??

    @objc func goTogetherBtn(_ sender: Any) {
        let firstActivityItem = "Text you want"
        let secondActivityItem : NSURL = NSURL(string: "http//:urlyouwant")!
        
        let activityViewController : UIActivityViewController = UIActivityViewController(
            activityItems: [firstActivityItem, secondActivityItem], applicationActivities: nil)
        activityViewController.popoverPresentationController?.sourceRect = CGRect(x: 150, y: 150, width: 0, height: 0)
        
        self.present(activityViewController, animated: true, completion: nil)
    }
    @objc func goBtn() {
        if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
//            UIApplication.shared.openURL(URL(string:
//                "comgooglemaps://?center=\(latitude),\(longitude)&zoom=14&views=traffic")!)
            UIApplication.shared.openURL(URL(string:
                "comgooglemaps://?saddr=\(slatitude),\(slongitude)&daddr=\(dlatitude),\(dlongitude)&center=37.423725,-122.0877&directionsmode=\(travelMode)&zoom=17")!)
        } else {
            print("Can't use comgooglemaps://");
        }

    }
    
    @objc func reRollBtn() {
        self.navigationController?.popViewController(animated: true)
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        placeName.text = name
        
        mapView.centerCoordinate = CLLocationCoordinate2D(latitude: Double(dlatitude) ?? Double(slatitude)!, longitude: Double(self.dlongitude) ?? Double(self.slongitude)!)
        let myAnnotation: MKPointAnnotation = MKPointAnnotation()
        myAnnotation.coordinate = CLLocationCoordinate2DMake(Double(dlatitude) ?? Double(slatitude)!, Double(dlongitude) ?? Double(slongitude)!)
        myAnnotation.title = name
        initialLocation = CLLocation(latitude: Double(self.dlatitude) ?? Double(self.slatitude)!, longitude: Double(self.dlongitude) ?? Double(self.slongitude)!)
        mapView.addAnnotation(myAnnotation)
        centerMapOnLocation(location: initialLocation as! CLLocation)
        placeName.fitTextToBounds()
        
        
        let singleTap = UITapGestureRecognizer(target: self, action: #selector(self.goBtn))
        goLabel.isUserInteractionEnabled = true
        goLabel.addGestureRecognizer(singleTap)
        
        let singleTap2 = UITapGestureRecognizer(target: self, action: #selector(self.goTogetherBtn))
        goTogetherLabel.isUserInteractionEnabled = true
        goTogetherLabel.addGestureRecognizer(singleTap2)
        
        let singleTap3 = UITapGestureRecognizer(target: self, action: #selector(self.reRollBtn))
        reRollLabel.isUserInteractionEnabled = true
        reRollLabel.addGestureRecognizer(singleTap3)
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
    
    let regionRadius: CLLocationDistance = 1000
    func centerMapOnLocation(location: CLLocation) {
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate,
                                                                  regionRadius, regionRadius)
        mapView.setRegion(coordinateRegion, animated: true)
    }

    
    
    
    

}
