#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>
#include <cmath> 



//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char ** argv)
{
  // check arguments
  if(argc != 3){
    std::cout << "usage: " << argv[0] << " [image_fond_vert] [image_remplacement]" << std::endl;
    return -1;
  }

  // load the first image
  std::cout << "load images ..." << std::endl;
  cv::Mat image = cv::imread(argv[1]);
  if(image.empty()){
    std::cout << "error loading " << argv[1] << std::endl;
    return -1;
  }

  // load the second image
  cv::Mat image2 = cv::imread(argv[2]);
  if(image2.empty()){
    std::cout << "error loading " << argv[2] << std::endl;
    return -1;
  }

  // display the image
  cv::namedWindow("une image");
  //cv::moveWindow("une image", 2000,20);
  cv::imshow("une image", image);
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::waitKey();
  
  // convert image to LAB
  cv::Mat imageLAB;
  cvtColor(image, imageLAB, cv::COLOR_BGR2Lab);

  // define target color
  cv::Vec3d target_color_Lab = imageLAB.at<cv::Vec3b>(42, 42);
  std::cout << "target color " << target_color_Lab << std::endl;



  // remove green here ...



  // display the image
  cv::imshow("une image", image);
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::waitKey();

  // save the image
  cv::imwrite("output/tp2ex3.jpg",image);

  return 1;
}
