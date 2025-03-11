#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

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

  // do something
  // ...

  auto pixel = image.at<cv::Vec3b>(100, 50); // (y, x) = (row, col)
  std::cout << "Pixel (50, 100) -> B: " << (int)pixel[0]
            << ", G: " << (int)pixel[1]
            << ", R: " << (int)pixel[2]
            << std::endl;

  // image.row(42) = cv::Scalar(0, 0, 255);
  for (auto col = 0; col < image.cols; col++)
  {
    image.at<cv::Vec3b>(42, col) = cv::Vec3b(0, 0, 255);
  }

  // Question 4:
  // On doit avoir des valeurs entre 0 et 255
  // En faisant +50, on peut dépasser 255 et changer complètement la couleur

  for (auto row = 0; row < image.rows; row++)
  {
    for (auto col = 0; col < image.cols; col++)
    {
      auto &pixel = image.at<cv::Vec3b>(row, col);
      for (auto color = 0; color < 3; color++)
      {
        // pixel[color] = std::min(std::max(pixel[color] + 50, 0), 255); // <- question 5
        // pixel[color] = pixel[color] + 50; // <- question 4
        pixel[color] = 255 - pixel[color]; // question 6
      }
    }
  }

  // Question 7
  // cv::Mat grey;
  // cv::cvtColor(image, grey, cv::COLOR_BGR2GRAY);
  // image = grey;

  // display an image
  cv::imshow("une image", image);
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::waitKey();

  // save the image
  cv::imwrite("output/tp1ex2.jpg", image);

  return 1;
}
