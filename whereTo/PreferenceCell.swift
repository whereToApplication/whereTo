//
//  PreferenceCell.swift
//  whereTo
//
//  Created by Krrish Dholakia on 11/11/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import UIKit

class PreferenceCell: UITableViewCell {

    @IBOutlet weak var placeCategory: UILabel!
    
    @IBOutlet weak var thumbsUp: UILabel!
    
    @IBOutlet weak var thumbsDown: UILabel!
    
    func setCategory(category: String) {
        placeCategory.text = category; 
    }
}