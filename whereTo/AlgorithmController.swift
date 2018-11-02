//
//  AlgorithmController.swift
//  whereTo
//
//  Created by Nirmit Shah on 10/20/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import Foundation
import UIKit
class AlgorithmController: UIViewController {
    var radius: String = ""
    var event: String = ""
    var travel: String = ""
    var place: String = ""
    
    @IBOutlet weak var checkrad: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        var row1: [Double] = [0.0, 2.0, 3.0];
        var row2: [Double] = [4.0, 0.0, 5.0];
        var row3: [Double] = [3.0, 2.0, 0.0];
        
        var inputarr: [[Double]] = [];
        inputarr.append(row1);
        inputarr.append(row2);
        inputarr.append(row3);
        var tester: HeldKarpTSPTrialVersion = HeldKarpTSPTrialVersion.init();
        
        tester.optimalRoute(distance: inputarr);
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}

