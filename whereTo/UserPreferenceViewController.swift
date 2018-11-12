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
    
    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.delegate = self;
        tableView.dataSource = self; 
        // Do any additional setup after loading the view.
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
        
        return cell;
    }
    
    
}
