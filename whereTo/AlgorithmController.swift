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

class AlgorithmController: UIViewController {
    var radius: String = ""
    var event: String = ""
    var travel: String = ""
    var place: String = ""
    var options1: [Place] = []
    @IBOutlet weak var checkrad: UILabel!
    let DISTMATRIX_BASE: String = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";
    let ORIG_PAR: String = "&origins=";
    let DEST_PAR: String = "&destinations=";
    let KEY_PAR: String = "&key=";
    let API_KEY: String = "AIzaSyAr-MIu6A-LmtXGsm94fDfIjICLguluajQ";
    
    func GoogleMapsDistanceMatrixAPI() -> [Int]{
        //var arr = Array(repeating: Array(repeating: 0, count: 2), count: 3)
        let options: [Place] = self.options1;
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
                            self.buildRoute(route: route);
                }
        });
        return route; 
    }
    
    func buildRoute(route: [Int]) {
        for i in 0...route.count - 1 {
            print(options1[route[i]].name);
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        GoogleMapsDistanceMatrixAPI();
        
//        var row1: [Double] = [0.0, 2.0, 3.0];
//        var row2: [Double] = [4.0, 0.0, 5.0];
//        var row3: [Double] = [3.0, 2.0, 0.0];
//
//        var inputarr: [[Double]] = [];
//        inputarr.append(row1);
//        inputarr.append(row2);
//        inputarr.append(row3);
//        var tester: HeldKarpTSPTrialVersion = HeldKarpTSPTrialVersion.init();
//
//        tester.optimalRoute(distance: inputarr);
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

