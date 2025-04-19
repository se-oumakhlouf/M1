#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>


//////////////////////////////////////////////////////////////////////////////////
void apply_component_scale(const cv::Mat &input, cv::Mat &output, const float r, const float g, const float b){

  output = input.clone(); // this is how opencv makes a copy of an image (don't use the operator '=' alone)

  for(int i=0; i<input.rows; ++i)
    for(int j=0; j<input.cols; ++j){
      output.at<cv::Vec3b>(i,j)[0] = (unsigned char) cv::saturate_cast<uchar>(input.at<cv::Vec3b>(i,j)[0] * r);
      output.at<cv::Vec3b>(i,j)[1] = (unsigned char) cv::saturate_cast<uchar>(input.at<cv::Vec3b>(i,j)[1] * g);
      output.at<cv::Vec3b>(i,j)[2] = (unsigned char) cv::saturate_cast<uchar>(input.at<cv::Vec3b>(i,j)[2] * b);
    }    
}


//////////////////////////////////////////////////////////////////////////////////
cv::Point3d grayWorld(const cv::Mat &image)
{
  return cv::Point3d(0.820032, 1, 1.35008);  // for wb_incandescent.png
}


//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char ** argv)
{
  // check arguments
  if(argc != 2){
    std::cout << "usage: " << argv[0] << " image" << std::endl;
    return -1;
  }

  // load the input image
  std::cout << "load image ..." << std::endl;
  cv::Mat image = cv::imread(argv[1]);
  if(image.empty()){
    std::cout << "error loading " << argv[1] << std::endl;
    return -1;
  }
  std::cout << "image size : " << image.cols << " x " << image.rows << std::endl;

  // display the image
  cv::namedWindow("une image");
  //cv::moveWindow("une image", 2000,20);
  cv::imshow("une image", image);
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::waitKey();



  // auto white balance here ...
  cv::Point3d wb = grayWorld(image);
  cv::Mat output;
  apply_component_scale(image, output, wb.x, wb.y, wb.z);



  // display the image
  cv::imshow("une image", output);
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::waitKey();

  // save the image
  cv::imwrite("output/tp2ex2.jpg",output);

  return 1;
}
