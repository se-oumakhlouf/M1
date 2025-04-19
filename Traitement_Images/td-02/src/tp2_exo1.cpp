#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>
#include <cmath>

void get_translation(cv::Mat &T, const float t_x, const float t_y)
{
  T = cv::Mat(3, 3, CV_32FC1, cv::Scalar(0.0f));
  cv::setIdentity(T);
  T.at<float>(0, 2) = t_x;
  T.at<float>(1, 2) = t_y;
}

void translate_image(cv::Mat &image, const float center_x, const float center_y)
{
  // translation matrix
  cv::Mat T;
  get_translation(T, center_x, center_y);

  // std::cout << T << std::endl;
  //  apply transformation
  cv::warpPerspective(image, image, T, image.size());
}

void get_rotation(cv::Mat &R, const float angle_in_degree)
{
  R = cv::Mat(3, 3, CV_32FC1, cv::Scalar(0.0f));
  const float radian = angle_in_degree * M_PI / 180.0;
  R.at<float>(0, 0) = std::cos(radian);
  R.at<float>(1, 1) = std::cos(radian);
  R.at<float>(1, 0) = std::sin(radian);
  R.at<float>(0, 1) = -std::sin(radian);
  R.at<float>(2, 2) = 1.0;
}

void rotate_image(cv::Mat &image, const float angle_in_degree)
{
  // rotation matrix
  cv::Mat R;
  get_rotation(R, angle_in_degree);

  // apply rotation
  cv::warpPerspective(image, image, R, image.size());
}

void rotate_image_around_point(cv::Mat &image, const float angle_in_degree, const float center_x, const float center_y)
{
  // rotation Matrix
  cv::Mat R;
  get_rotation(R, angle_in_degree);

  // translation Matrix
  cv::Mat T;
  get_translation(T, center_x, center_y);

  // inverse translation Matrix;
  cv::Mat Inverse;
  get_translation(Inverse, -center_x, -center_y);

  // application Matrix
  cv::Mat Combine = T * R * Inverse;

  // apply transformation
  cv::warpPerspective(image, image, Combine, image.size());
}

void q0(cv::Mat &image)
{
  std::cout << "appuyer sur une touche pour afficher première translation ..." << std::endl;
  cv::waitKey();
  translate_image(image, 200, 10);
  cv::imshow("une image", image);
}

void q2(cv::Mat &image)
{
  translate_image(image, -200, -10);
  std::cout << "appuyer sur une touche pour afficher la translation inverse ..." << std::endl;
  cv::waitKey();
  cv::imshow("une image", image);
}

void q3_4(cv::Mat &image, const float angle_in_degree)
{
  rotate_image(image, angle_in_degree);
  std::cout << "appuyer sur une touche pour afficher la rotation ..." << std::endl;
  cv::waitKey();
  cv::imshow("une image", image);
}

void q5(cv::Mat &image, const float angle_in_degree, const float center_x, const float center_y)
{
  rotate_image_around_point(image, angle_in_degree, center_x, center_y);
  std::cout << "appuyer sur une touche effectuer une rotation  de " << angle_in_degree << "° autour du point (" << center_x << ", " << center_y << ") ..." << std::endl;
  cv::waitKey();
  cv::imshow("une image", image);
}

void q6(cv::Mat &image)
{
  std::cout << "Appuyer sur une touche pour effectuer 20 rotations centrées successives de 10° chacune ..." << std::endl;
  cv::waitKey();
  for (int i = 0; i < 20; i++)
  {
    rotate_image_around_point(image, 10, image.cols / 2, image.rows / 2);
  }
  cv::imshow("une image", image);

  std::cout << "Appuyer sur une touche pour effectuer 20 rotations centrées successives de -10° chacune ..." << std::endl;
  cv::waitKey();
  for (int i = 0; i < 20; i++)
  {
    rotate_image_around_point(image, -10, image.cols / 2, image.rows / 2);
  }
  cv::imshow("une image", image);
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

  // display the image
  cv::namedWindow("une image");
  // cv::moveWindow("une image", 2000,20);
  cv::imshow("une image", image);

  // Première translation
  // q0(image);

  /*
    Question 2 : translation inverse
    Lorsqu'une portion sort du champ visible, elle est définitivement supprimé et non restaurée
    même si on revient en arrière
  */
  // q2(image);

  /*
    Question 3 - 4 : rotation autour de l'origine (0, 0)
    angle de 30°
  */
  // q3_4(image, 30);

  /*
    Question 5 : rotation autour d'un point quelconque passé en paramètre
  */
  // q5(image, 180, 450, 300);

  /*
    Question 6 : 20 rotations de 10° puis 20 rotations de - 10° autour du centre

    On s'attend à retrouver l'image initial après les 40 rotations, cependant :
      - à force d'appliquer des traitements on perd de la qualité d'image
      - à chaque rotation, des bords sortent du cadre, donc les pixels sont perdus
  */
  q6(image);

  // fin
  std::cout << "appuyer sur une touche pour quitter le programme ..." << std::endl;
  cv::waitKey();

  // save the image
  // cv::imwrite("output/tp2ex1.jpg",image);
  cv::imwrite("/home/M1/Traitement_Images/td-02/output/tp2ex1.jpg", image);

  return 1;
}
