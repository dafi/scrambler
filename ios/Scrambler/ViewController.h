//
//  ViewController.h
//  Scrambler
//
//  Created by davide ficano on 06/05/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController<UITextFieldDelegate> {
    IBOutlet UIImageView* imageView;
    IBOutlet UITextField* piecesTextField;
}

@property (strong) UIImage* currentImage;
@end
