//
//  UserPreferenceViewController.swift
//  whereTo
//
//  Created by Krrish Dholakia on 11/11/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import UIKit
class UserPreferenceViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    var categories: [String] = [];
    var places: [Place] = [];
    var radiusText: String = "";
    var eventsText: String = "";
    var paceText: String = "";
    var timeText: String = "";
    var k = 9;
    var spotList: [Place] = [];
    var voteUpCategories: Set<String> = Set<String>.init();
    var voteDownCategories: Set<String> = Set<String>.init();
    
    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.delegate = self;
        tableView.dataSource = self;
        // Do any additional setup after loading the view.
    }
    
    
    @IBAction func onDoneBtnClicked(_ sender: Any) {
        var PopularityDic = [String: Double]();
        var ratingsum = 0.0;
        
        var placesVotedDownCategories : [Int] = Array(repeating: 0, count: places.count);
        var placesVotedUpCategories: [Int] = Array(repeating: 0, count: places.count);
        var count = 0;
        
        for place in places {
            PopularityDic[place.name] = Double(place.rating);
            ratingsum += Double(place.rating);
        }
        var factor = 1.0/ratingsum;
        for place in places {
            PopularityDic[place.name] = PopularityDic[place.name]! * factor;
            
            //create freq. counter for the weighting of the places
            for category in place.categories {
                if voteUpCategories.contains(category) {
                    placesVotedUpCategories[count] = placesVotedUpCategories[count] + 1;
                } else if voteDownCategories.contains(category) {
                    placesVotedDownCategories[count] = placesVotedDownCategories[count] + 1;
                }
            }
            
            count += 1;
        }
        
        //check if downvotes
        count = 0;
        ratingsum = 0.0;
        for place in places {
            if placesVotedDownCategories[count] > 0 {
                PopularityDic[place.name] = PopularityDic[place.name]! * exp(Double(-1 * placesVotedDownCategories[count]));
            }
            ratingsum += PopularityDic[place.name] ?? 1.0;
            count += 1;
        }
        
//        //normalize
//        factor = 1.0/ratingsum;
//        for place in places {
//            PopularityDic[place.name] = PopularityDic[place.name]! * factor;
//        }
//
        //check if upvotes
        count = 0;
        ratingsum = 0.0;
        for place in places {
            if placesVotedUpCategories[count] > 0 {
                PopularityDic[place.name] = PopularityDic[place.name]! * exp(Double(1 * placesVotedUpCategories[count]));
            }
            ratingsum += PopularityDic[place.name] ?? 1.0;
            count += 1;
        }
        
        //normalize
        factor = 1.0/ratingsum;
        for place in places {
            PopularityDic[place.name] = PopularityDic[place.name]! * factor;
        }
        
        //replace rating in place.rating w/ popularity dict value
        places = places.sorted {PopularityDic[$0.name]! > PopularityDic[$1.name]!}
        let element = places.remove(at: places.count - 1)
        places.insert(element, at: 0)
        
        if self.k > places.count {
            self.spotList = places;
        } else {
            for count in 0 ... self.k  {
                self.spotList.append(places[count]);
            }

        }
        
        performSegue(withIdentifier: "tableToAlgoIdentifier", sender: self)

    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destination = segue.destination as? AlgorithmController {
            destination.radius = self.radiusText
            destination.event = self.eventsText
            destination.pace = self.paceText
            destination.options1 = self.spotList
            destination.time = self.timeText
            destination.k = self.k; 
        }
    }
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}

extension UserPreferenceViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return categories.count;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let category = self.categories[indexPath.row]
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "PreferenceCell") as! PreferenceCell
        
        cell.setCategory(category: category)
        
        let thumbsUpTap = CustomTapGestureRecognizer(target: self, action: #selector(self.onThumbsUp))
        thumbsUpTap.category = category;
        cell.thumbsUp.isUserInteractionEnabled = true
        cell.thumbsUp.addGestureRecognizer(thumbsUpTap);
        
        let thumbsDownTap = CustomTapGestureRecognizer(target: self, action: #selector(self.onThumbsDown))
        thumbsDownTap.category = category;
        cell.thumbsDown.isUserInteractionEnabled = true
        cell.thumbsDown.addGestureRecognizer(thumbsDownTap)
        
        return cell;
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 200.0;
    }
    
    
    @objc func onThumbsUp(sender: CustomTapGestureRecognizer) {
        if let category = sender.category {
            voteUpCategories.insert(category);
            if voteDownCategories.contains(category) {
                voteDownCategories.remove(category);
            }
        }
    }
    
    @objc func onThumbsDown(sender: CustomTapGestureRecognizer) {
        if let category = sender.category {
            voteDownCategories.insert(category);
            if voteUpCategories.contains(category) {
                voteUpCategories.remove(category);
            }
        }
    }
    
}

class CustomTapGestureRecognizer: UITapGestureRecognizer {
    var category: String?
}
