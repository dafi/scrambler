//
//  ViewController.m
//  Scrambler
//
//  Created by davide ficano on 06/05/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "ViewController.h"
#import "ScrambleUtils.h"
#import <ImageIO/CGImageSource.h>

@interface ViewController ()
- (UIImage*)loadImageFromURL:(NSString*)path;
@end

@implementation ViewController

@synthesize currentImage = _currentImage;

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.currentImage = [self loadImageFromURL:@"http://farm3.staticflickr.com/2457/3697466514_721bd9c533_d.jpg"];
    [piecesTextField setText:@"3"];
    imageView.image = [ScrambleUtils scrambleImage:self.currentImage :3];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
    } else {
        return YES;
    }
}

- (IBAction)scramble:(id)sender {
    int pieces = [[piecesTextField text] intValue];
    imageView.image = [ScrambleUtils scrambleImage:self.currentImage :pieces];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    // hide keyboard when user press ENTER
    [piecesTextField resignFirstResponder];
    [self scramble:nil];
    return YES;
}

// Require the framework ApplicationServices/ImageIO

- (UIImage*)loadImageFromURL:(NSString*)path {
    NSURL* url = [NSURL URLWithString:path];
    
    CGImageSourceRef imageSource = CGImageSourceCreateWithURL((__bridge CFURLRef)url, nil);

    if (imageSource == NULL) {
        return nil;
    }
    // Create an image from the first item in the image source.
    CGImageRef imageRef = CGImageSourceCreateImageAtIndex(imageSource,
                                              0,
                                              NULL);
    
    UIImage* image = [UIImage imageWithCGImage:imageRef];

    CFRelease(imageSource);
    CFRelease(imageRef);
    
    return image;
}
@end
