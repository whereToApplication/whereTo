//
//  Place.swift
//  whereTo
//
//  Created by Krrish Dholakia on 11/2/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//
import Foundation;

struct Place {
    var name: String = "";
    var coordinated: coordinates = coordinates(latitude: 0.0, longitude: 0.0);
    var rating: Int = 0;
    var review_count: Int = 0;
    var categories: [String] = [];
    init(name: String, coordinates: coordinates, rating: Int, review_count: Int, categories: [String]) {
        self.name = name;
        self.coordinated.latitude = coordinates.latitude;
        self.coordinated.longitude = coordinates.longitude;
        self.rating = rating;
        self.review_count = review_count;
        self.categories = categories;
    }
    
}


struct coordinates {
    var latitude: Double = 0.0;
    var longitude: Double = 0.0;
    
    init(latitude: Double, longitude: Double) {
        self.latitude = latitude;
        self.longitude = longitude;
    }
}

