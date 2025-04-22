#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc.hpp>

#include <iostream>

//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char **argv)
{
  // check arguments
  if (argc != 2)
  {
    std::cout << "usage: " << argv[0] << " image" << std::endl;
    return -1;
  }

  // load the input image
  std::cout << "load image ..." << std::endl;
  cv::Mat image = cv::imread(argv[1]);
  if (image.empty())
  {
    std::cout << "error loading " << argv[1] << std::endl;
    return -1;
  }
  std::cout << "image size : " << image.cols << " x " << image.rows << std::endl;

  // convert to gray scale
  cv::cvtColor(image, image, cv::COLOR_RGB2GRAY);
  cv::Mat outputImage;

  // cv::Mat structuringElement = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(5, 5));
  // cv::dilate(image, outputImage, structuringElement);

  /*
    Exercice 1 : Dilatation / Erosion
  */
  // cv::Mat structuringElement = cv::getStructuringElement(cv::MORPH_CROSS, cv::Size(9, 9));
  // cv::dilate(image, outputImage, elem);
  // cv::erode(outputImage, outputImage, elem);

  /*
    Exercice 2 : Ouverture / Fermeture
  */
  // cv::morphologyEx(image, outputImage, cv::MORPH_OPEN, structuringElement);
  // cv::morphologyEx(image, outputImage, cv::MORPH_CLOSE, structuringElement);

  /*
    Exercice 3 : En pratique
  */
  // Question 1 : Permet d'enlever les petits trous (tout en gardant les plus grands) et l'anneau noir de l'image3.png
  // cv::Mat elem = cv::getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(15, 15));
  // cv::morphologyEx(image, outputImage, cv::MORPH_CLOSE, elem);

  // Question 2 : ne garder que les grands trous noirs
  

  // Question 3 : ne garder que les petits trous
  image = 255 - image;
  cv::Mat elem = cv::getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(15, 15));
  cv::morphologyEx(image, outputImage, cv::MORPH_OPEN, elem);
  cv::Mat other = image - outputImage;
  
  // display everything
  cv::imshow("image", image);
  cv::waitKey();
  cv::imshow("image", outputImage);
  cv::waitKey();
  cv::imshow("image", other);
  cv::waitKey();

  return 0;
}
