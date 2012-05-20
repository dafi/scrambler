//
//  ScrambleUtils.m
//  Scrambler
//
//  Created by davide ficano on 07/05/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ScrambleUtils.h"

@implementation ScrambleUtils

+ (int*)arrayRange:(int)count {
    int* arr = malloc(sizeof(int) * count);
    
    for (int i = 0; i < count; i++) {
        arr[i] = i;
    }
    
    return arr;
}

+ (int*)shuffle:(int*)arr :(int)count {
    for (int i = count - 1; i >= 0; --i) {
        // swap indexes
        int r = i ? rand() % i : 0;
        int t = arr[i];
        arr[i] = arr[r];
        arr[r] = t;
    }
    
    return arr;
}

+ (UIImage*)scrambleImage:(UIImage*)image :(int)piecesPerLine {
    CGFloat px = ceilf(image.size.width / (CGFloat)piecesPerLine);
    CGFloat ph = ceilf(image.size.height / (CGFloat)piecesPerLine);
    int count = piecesPerLine * piecesPerLine;
    int* arr = [ScrambleUtils shuffle:[ScrambleUtils arrayRange:count] :count];
    
    UIGraphicsBeginImageContext(image.size);
    CGImageRef imageRef = image.CGImage;
    
    for (int i = 0; i < piecesPerLine; i++) {
        for (int j = 0; j < piecesPerLine; j++) {
            int n = arr[i * piecesPerLine +  j];
            int row = n / piecesPerLine;
            int col = n % piecesPerLine;
            
            CGRect fromRect = CGRectMake(row * px, col * ph, px, ph);
            CGRect toRect = CGRectMake(j * px, i * ph, px, ph);
            CGImageRef subImageRef = CGImageCreateWithImageInRect(imageRef, fromRect);
            UIImage* subImage = [UIImage imageWithCGImage:subImageRef];
            [subImage drawInRect:toRect];
            CFRelease(subImageRef);
        }
    }
    free(arr);
    UIImage* resultImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return resultImage;
}

@end
