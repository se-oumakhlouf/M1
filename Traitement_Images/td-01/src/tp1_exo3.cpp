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

  auto mid_col = image.cols / 2;
  auto quarter_col = mid_col / 2;

  auto mid_row = image.rows / 2;
  auto quarter_row = mid_row / 2;

  for (auto row = quarter_row; row < mid_row + quarter_row; row++)
  {
    for (auto col = quarter_col; col < mid_col + quarter_col; col++)
    {
      auto gray = (col - quarter_col) % 255;
      image.at<cv::Vec3b>(row, col) = cv::Vec3b(gray, gray, gray); // dégradé noir / blanc

      // image.at<cv::Vec3b>(row, col) = cv::Vec3b(0, 255, 0); // carré vert plein
    }
  }

  cv::Point center {mid_col, mid_row};
  const cv::Scalar yellow {0, 255, 255};
  cv::circle(image, center, quarter_col, yellow, 2);

  // display an image
  cv::imshow("une image", image);
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::waitKey();

  // save the image
  cv::imwrite("output/tp1ex3.jpg", image);

  return 1;
}
