//
//  optionsViewController.swift
//  whereTo
//
//  Created by Nirmit Shah on 10/20/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import Foundation
import UIKit
class optionsViewController: UIViewController {
    var radiusText: String = ""
    var eventsText: String = ""
    var travelTypeText: String = ""
    var placeText: String = ""
    @IBOutlet weak var datePicker: UIDatePicker!
    @IBAction func eventAction(_ sender: UISegmentedControl) {
        eventsText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
    }
    @IBOutlet weak var radiusPicker: UISegmentedControl!
    @IBAction func radiusAction(_ sender: UISegmentedControl) {
        radiusText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
    }
    @IBOutlet weak var eventPicker: UISegmentedControl!
    
    @IBAction func travelAction(_ sender: UISegmentedControl) {
            travelTypeText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
    }
    @IBAction func paceAction(_ sender: UISegmentedControl) {
        placeText = sender.titleForSegment(at: sender.selectedSegmentIndex) ?? ""
    }
    @IBOutlet weak var travelPicker: UISegmentedControl!
    @IBOutlet weak var pacePicker: UISegmentedControl!

   

    @IBAction func submit(_ sender: UIButton) {
        if radiusText.count > 0 && eventsText.count > 0 && travelTypeText.count > 0 && placeText.count > 0 {
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
            destination.place = self.placeText
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
//    let radii = ["Nearby", "Whole City"]
//    let events = ["Sightsee", "Food", "Both"]
//    let travels = ["Walk, Drive, Transit", "Any"]
//    let paces = ["Fast", "Normal", "Slow"]
    //what i want
    //once all of the segments are selected, enable the done button. Then pass in the radius, event, travel, and pace that were selected. Then go to the algorithm.
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        }
}
