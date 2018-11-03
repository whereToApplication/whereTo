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
    @IBOutlet var loadingView: UIView!
    @IBOutlet weak var labelToFade: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        showLoadingScreen()
        
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
    func showLoadingScreen() {
        loadingView.bounds.size.width = view.bounds.width - 25;
        loadingView.bounds.size.height = view.bounds.height - 40;
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

