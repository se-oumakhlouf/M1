#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <iostream>

#include "fourierTransform.hpp"

cv::Mat sobelXFourierKernel(const int width, const int height)
{
  cv::Mat kernel(height, width, CV_32F, cv::Scalar(0.0));

  int half_height = height / 2;
  int half_width = width / 2;

  kernel.at<float>(half_height - 1, half_width - 1) = -1.0;
  kernel.at<float>(half_height - 1, half_width + 1) = 1.0;

  kernel.at<float>(half_height, half_width - 1) = -2.0;
  kernel.at<float>(half_height, half_width + 1) = 2.0;

  kernel.at<float>(half_height + 1, half_width - 1) = -1.0;
  kernel.at<float>(half_height + 1, half_width + 1) = 1.0;

  return kernel;
}

cv::Mat sobelYFourierKernel(const int width, const int height)
{
  cv::Mat kernel(height, width, CV_32F, cv::Scalar(0.0));

  int half_height = height / 2;
  int half_width = width / 2;

  kernel.at<float>(half_height - 1, half_width - 1) = -1.0;
  kernel.at<float>(half_height - 1, half_width) = -2.0;
  kernel.at<float>(half_height - 1, half_width + 1) = -1.0;

  kernel.at<float>(half_height + 1, half_width - 1) = 1.0;
  kernel.at<float>(half_height + 1, half_width) = 2.0;
  kernel.at<float>(half_height + 1, half_width + 1) = 1.0;

  return kernel;
}

cv::Mat laplacianFourierKernel(const int width, const int height)
{
  cv::Mat kernel(height, width, CV_32F, cv::Scalar(0.0));

  kernel.at<float>((height / 2) - 1, (width / 2)) = 1.0;

  kernel.at<float>((height / 2), (width / 2) - 1) = 1.0;
  kernel.at<float>((height / 2), (width / 2)) = -4.0;
  kernel.at<float>((height / 2), (width / 2) + 1) = 1.0;

  kernel.at<float>((height / 2) + 1, (width / 2)) = 1.0;

  return kernel;
}

cv::Mat rehausseurContoursKernel(const int width, const int height)
{
  cv::Mat kernel(height, width, CV_32F, cv::Scalar(0.0));

  kernel.at<float>((height / 2) - 1, (width / 2)) = -1.0;

  kernel.at<float>((height / 2), (width / 2) - 1) = -1.0;
  kernel.at<float>((height / 2), (width / 2)) = 5.0;
  kernel.at<float>((height / 2), (width / 2) + 1) = -1.0;

  kernel.at<float>((height / 2) + 1, (width / 2)) = -1.0;

  return kernel;
}

cv::Mat gaussianFourierKernel(const int width, const int height, const double sigma)
{
  cv::Mat kernel(height, width, CV_32F, cv::Scalar(0.0));

  float sum = 0.0;
  for (int i = 0; i < height; ++i)
    for (int j = 0; j < width; ++j)
    {
      float squareDist = pow((-height * 0.5) + i, 2) + pow((-width * 0.5) + j, 2);
      kernel.at<float>(i, j) = exp(-squareDist / (2.0 * sigma * sigma));
      sum += kernel.at<float>(i, j);
    }

  // kernel normalisation
  kernel = kernel / sum;

  return kernel;
}

void applyKernel(cv::Mat &magnitude, const cv::Mat &kernel)
{
  for (int i = 0; i < magnitude.rows; i++)
  {
    for (int j = 0; j < magnitude.cols; j++)
    {
      magnitude.at<float>(i, j) *= kernel.at<float>(i, j);
    }
  }
}

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

  // discrete Fourier Transform
  cv::Mat imageFourierMagnitude, imageFourierPhase;
  discreteFourierTransform(image, imageFourierMagnitude, imageFourierPhase);
  cv::imwrite("/home/M1/Traitement_Images/td-05/output/magnitude_input.png", fourierMagnitudeToDisplay(imageFourierMagnitude));

  /*
    Exercice 1 : rayure
  */

  // float max_radius_image = std::sqrt(image.cols * image.cols + image.rows * image.rows);

  // std::cout << "rayures ..." << std::endl;
  // imageFourierMagnitude.at<float>(imageFourierMagnitude.rows / 2, 10 + imageFourierMagnitude.cols / 2) = 0;
  // imageFourierMagnitude.at<float>(imageFourierMagnitude.rows / 2, -10 + imageFourierMagnitude.cols / 2) = 0;

  /* Exercice 2 : filtre passe bas / filtre passe haut / filtre passe bande */

  // filtre passe bas
  // std::cout << "filtre passe bas ..." << std::endl;
  // float minRadius = 10;
  // float maxRadius = max_radius_image;
  // removeRing(imageFourierMagnitude, minRadius, maxRadius);

  // filtre passe haut
  // std::cout << "filtre passe haut ..." << std::endl;
  // float minRadius = 1;
  // float maxRadius = 15;
  // removeRing(imageFourierMagnitude, minRadius, maxRadius);

  // filtre passe bande
  // std::cout << "filtre passe bande ..." << std::endl;
  // float minRadius = 20;
  // float maxRadius = 30;
  // removeRing(imageFourierMagnitude, 0, minRadius);
  // removeRing(imageFourierMagnitude, maxRadius, max_radius_image);

  /* Exercice 3 : filtre de convolution */

  // Sobel X repère là ou l'image change fortement de gauche à droite
  // cv::Mat kernel = sobelXFourierKernel(imageFourierMagnitude.cols, imageFourierMagnitude.rows);
  cv::Mat kernel = sobelYFourierKernel(imageFourierMagnitude.cols, imageFourierMagnitude.rows);
  // cv::Mat kernel = laplacianFourierKernel(imageFourierMagnitude.cols, imageFourierMagnitude.rows);
  // cv::Mat kernel = rehausseurContoursKernel(imageFourierMagnitude.cols, imageFourierMagnitude.rows);
  // cv::Mat kernel = gaussianFourierKernel(imageFourierMagnitude.cols, imageFourierMagnitude.rows, std::sqrt(8));

  cv::Mat kernelFourierMagnitude, kernelFourierPhase;
  discreteFourierTransform(kernel, kernelFourierMagnitude, kernelFourierPhase);

  applyKernel(imageFourierMagnitude, kernelFourierMagnitude);

  // inverse Fourier Transform
  cv::Mat outputImage;
  inverseDiscreteFourierTransform(imageFourierMagnitude, imageFourierPhase, outputImage, CV_8U);

  // display everything
  cv::namedWindow("Input image");
  cv::namedWindow("Magnitude");
  cv::namedWindow("Output image");

  cv::moveWindow("Input image", 100, 50);
  cv::moveWindow("Magnitude", 700, 50);
  cv::moveWindow("Output image", 100, 400);

  cv::imshow("Input image", image);
  cv::imshow("Magnitude", fourierMagnitudeToDisplay(imageFourierMagnitude));
  cv::imshow("Output image", outputImage);
  cv::waitKey();

  // save the images
  std::string output_folder = "/home/M1/Traitement_Images/td-05/output/";
  cv::imwrite(output_folder + "inputImage.jpg", image);
  cv::imwrite(output_folder + "kernel_magnitude.png", fourierMagnitudeToDisplay(kernelFourierMagnitude));
  cv::imwrite(output_folder + "magnitude.png", fourierMagnitudeToDisplay(imageFourierMagnitude));
  cv::imwrite(output_folder + "phase.png", fourierPhaseToDisplay(imageFourierPhase));
  cv::imwrite(output_folder + "filteredImage.png", outputImage);

  return 0;
}
