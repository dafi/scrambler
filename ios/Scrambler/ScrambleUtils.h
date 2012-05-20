//
//  ScrambleUtils.h
//  Scrambler
//
//  Created by davide ficano on 07/05/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ScrambleUtils : NSObject

+ (int*)arrayRange:(int)count;
+ (int*)shuffle:(int*)arr :(int)count;
+ (UIImage*)scrambleImage:(UIImage*)image :(int)piecesPerLine;

@end
