//
//  AnimatedGradientPathRenderer.swift
//  whereTo
//
//  Created by Krrish Dholakia on 11/6/18.
//  Copyright © 2018 Krrish Dholakia. All rights reserved.
//

import Foundation
import MapKit;
import CoreGraphics;

class AnimatedGradientPathRenderer: MKPolylineRenderer {
    override func draw(_ mapRect: MKMapRect, zoomScale: MKZoomScale, in context: CGContext) {
        
        var bezierPath: UIBezierPath = UIBezierPath.init();
        
        bezierPath.move(to: self.point(for: MKMapPointForCoordinate(polyline.coordinates[0])));
        
        for i in polyline.coordinates[1...] {
            bezierPath.addLine(to: self.point(for: MKMapPointForCoordinate(i)));
        }
        
        bezierPath.close();
        
        context.addPath(bezierPath.cgPath);
        context.setStrokeColor(UIColor.red.cgColor);
        context.setLineWidth(4);
        context.drawPath(using: .stroke)
        
        return;
    }
}

public extension MKMultiPoint {
    var coordinates: [CLLocationCoordinate2D] {
        var coords = [CLLocationCoordinate2D](repeating: kCLLocationCoordinate2DInvalid,
                                              count: pointCount)
        
        getCoordinates(&coords, range: NSRange(location: 0, length: pointCount))
        
        return coords
    }
}
