//
//  AlgorithmController.swift
//  whereTo
//
//  Created by Nirmit Shah on 10/20/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import Foundation
import UIKit
import SwiftyJSON
import Alamofire
import MapKit

class AlgorithmController: UIViewController, CLLocationManagerDelegate {
    @IBOutlet weak var mapView: MKMapView!
    var radius: String = ""
    var event: String = ""
    var travel: String = ""
    var place: String = ""
    var options1: [Place] = []
    @IBOutlet var loadingView: UIView!
    @IBOutlet weak var labelToFade: UILabel!
    let DISTMATRIX_BASE: String = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    let ORIG_PAR: String = "&origins=";
    let DEST_PAR: String = "&destinations=";
    let KEY_PAR: String = "&key=";
    let API_KEY: String = "AIzaSyAr-MIu6A-LmtXGsm94fDfIjICLguluajQ";
    
    
    var initialLocation : CLLocation??

    
    func GoogleMapsDistanceMatrixAPI() -> [Place]{
        //var arr = Array(repeating: Array(repeating: 0, count: 2), count: 3)
        var options: [Place] = self.options1;
        var distMatrix = Array(repeating: Array(repeating: 0.0, count: options.count), count: options.count);
        var route: [Int] = [];
        var origdest: String = "";
        
        if options.count != 0 {
            origdest.append("\(options[0].coordinated.latitude)");
            origdest.append(",");
            origdest.append("\(options[0].coordinated.longitude)");
            for i in 1 ... options.count - 1 {
                origdest.append("|");
                origdest.append("\(options[i].coordinated.latitude)");
                origdest.append(",");
                origdest.append("\(options[i].coordinated.longitude)");
            }
        }
        let urlString: String = DISTMATRIX_BASE + ORIG_PAR + origdest + DEST_PAR + origdest + KEY_PAR + API_KEY;
        let encodedUrl = urlString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)
        
        let header: HTTPHeaders = [ "Accept": "application/json", "Content-Type": "application/json" ]
        
        Alamofire.request(encodedUrl!, method: HTTPMethod.get, encoding: JSONEncoding.default, headers: header).responseJSON(completionHandler: {
            
            response in
                        if let jsonValue = response.result.value {
                            let json = SwiftyJSON.JSON(jsonValue)
                            print(json["rows"])
                            for i in 0...options.count - 1 {
                                for j in 0...options.count - 1 {
                                    distMatrix[i][j] = json["rows"][i]["elements"][j]["duration"]["value"].doubleValue;
                                }
                            }
                            
                            var tester: HeldKarpTSPTrialVersion = HeldKarpTSPTrialVersion.init();
                            var optimalRoute = tester.optimalRoute(distance: distMatrix);
                            route = optimalRoute[1] as! [Int];
                            options = self.buildRoute(route: route);
                }
        });
        return options;
    }
    
    func buildRoute(route: [Int]) -> [Place] {
        var optimalroutes: [Place] = [];
        for i in 0...route.count - 1 {
            optimalroutes.append(options1[route[i]]);
        }
        
        var locations = optimalroutes.map { CLLocationCoordinate2D.init(latitude: $0.coordinated.latitude, longitude: $0.coordinated.longitude) }
        let polyline = MKPolyline(coordinates: &locations, count: locations.count)
        mapView?.add(polyline);
        
        var urlString: String = "comgooglemaps://?daddr="
        
        for i in 1...locations.count-1 {
            urlString.append("\(locations[i].latitude),\(locations[i].longitude)+to:");
        }
        
//        DispatchQueue.main.asyncAfter(deadline: .now() + 4.5) {
//            if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
//                //            UIApplication.shared.openURL(URL(string:
//                //                "comgooglemaps://?center=\(latitude),\(longitude)&zoom=14&views=traffic")!)
//
//
////                UIApplication.shared.openURL(URL(string:
////                    "comgooglemaps://?saddr=\(locations[1].latitude),\(locations[)&daddr=\(dlatitude),\(dlongitude)&center=37.423725,-122.0877&directionsmode=\(travelMode)&zoom=17")!)
////            } else {
////                print("Can't use comgooglemaps://");
////            }
//        }
//
//        /*
//
//         if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
//         //            UIApplication.shared.openURL(URL(string:
//         //                "comgooglemaps://?center=\(latitude),\(longitude)&zoom=14&views=traffic")!)
//         UIApplication.shared.openURL(URL(string:
//         "comgooglemaps://?saddr=\(slatitude),\(slongitude)&daddr=\(dlatitude),\(dlongitude)&center=37.423725,-122.0877&directionsmode=\(travelMode)&zoom=17")!)
//         } else {
//         print("Can't use comgooglemaps://");
//         }
//
//         */
        urlString.removeSubrange(urlString.index(urlString.endIndex, offsetBy: -4)...urlString.index(urlString.endIndex, offsetBy: -1));
        
        
        if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
                     //            UIApplication.shared.openURL(URL(string:
                     //                "comgooglemaps://?center=\(latitude),\(longitude)&zoom=14&views=traffic")!)
                     UIApplication.shared.openURL(URL(string:
                     "comgooglemaps://daddr=\(urlString)&center=37.423725,-122.0877&directionsmode=car&zoom=17")!)
                     } else {
                     print("Can't use comgooglemaps://");
                     }
        return optimalroutes;
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        mapView?.delegate = self;
        let optimalRoutes = GoogleMapsDistanceMatrixAPI();
        showLoadingScreen()
        
        //set up markers on map
        for i in 0...options1.count - 1 {
            mapView.centerCoordinate = CLLocationCoordinate2D(latitude: options1[i].coordinated.latitude, longitude: options1[i].coordinated.longitude);
            let myAnnotation: MKPointAnnotation = MKPointAnnotation()
            myAnnotation.coordinate = CLLocationCoordinate2DMake(options1[i].coordinated.latitude, options1[i].coordinated.longitude)
            myAnnotation.title = options1[i].name
            mapView.addAnnotation(myAnnotation)
            
        }
        
        
        var zoomRect: MKMapRect = MKMapRectNull;
        
        for annotation in mapView.annotations {
            let annotationPoint: MKMapPoint = MKMapPointForCoordinate(annotation.coordinate);
            let pointRect: MKMapRect = MKMapRectMake(annotationPoint.x, annotationPoint.y, 0, 0);
            if (MKMapRectIsNull(zoomRect)) {
                zoomRect = pointRect;
            } else {
                zoomRect = MKMapRectUnion(zoomRect, pointRect);
            }
        }
        [mapView?.setVisibleMapRect(zoomRect, animated: true)];
        
        
       
//        initialLocation = CLLocation(latitude: options1[0].coordinated.latitude, longitude: options1[0].coordinated.longitude)
//        centerMapOnLocation(location: initialLocation as! CLLocation)
        
    }
    
    
    let regionRadius: CLLocationDistance = 1000
    func centerMapOnLocation(location: CLLocation) {
        let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate,
                                                                  regionRadius, regionRadius)
        mapView.setRegion(coordinateRegion, animated: true)
    }
    
    func showLoadingScreen() {
        loadingView.bounds.size.width = view.bounds.width;
        loadingView.bounds.size.height = view.bounds.height;
        loadingView.center = view.center;
        loadingView.alpha = 0;
//        labelToFade.alpha = 1;
        view.addSubview(loadingView);
        UIView.animate(withDuration: 0.7, delay: 0.3, options: [], animations: {
            self.loadingView.alpha = 1
        }) {(success) in
//            var i = 0;
//            while(i < 3) {
//                UIView.animate(withDuration: 0.6, delay: 0.7, animations: {
//                        self.labelToFade.alpha = 0.0
//                }, completion: {(success) in
//                    UIView.animate(withDuration: 0.6, animations: {
//                            self.labelToFade.alpha = 1.0
//                    })
//                })
//                i = i + 1;
//            }
//            UIView.animate(withDuration: 1.0,
//                           delay: 0,
//                           options: [.autoreverse, .repeat],
//                           animations: {
//                            self.labelToFade.alpha = 1 - self.labelToFade.alpha
//            },
//                           completion: nil
//
//            )
//            var i = 0;
//            while(i < 3) {
                UIView.animate(withDuration: 1.0, delay: 1.0, options: UIViewAnimationOptions.curveEaseOut, animations: {
                    self.labelToFade.alpha = 1.0
                }, completion: nil)
                
                UIView.animate(withDuration: 1.0, delay: 1.0, options: UIViewAnimationOptions.curveEaseIn, animations: {
                    self.labelToFade.alpha = 0.0
                }, completion: {(success) in
                    UIView.animate(withDuration: 1.0
                        , animations: {
                            self.loadingView.alpha = 0.0
                            
                    })
                })
//                i = i + 1;
//            }
        }
        
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

extension AlgorithmController: MKMapViewDelegate {
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
            let renderer = MKPolylineRenderer(overlay: overlay)
            renderer.strokeColor = UIColor.orange
            renderer.lineWidth = 3
            return renderer
    }

}
