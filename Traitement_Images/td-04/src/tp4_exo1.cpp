#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>

///////////////////////////////////////////////////////////////////////////////
/*
  Ce noyau :
    [ 0  0  0 ]
    [ 0 -1  1 ]
    [ 0  0  0 ]

  Calcule la différence entre le pixel de droite et celui du centre → dérivée horizontale (X).
  Cela permet de détecter les changements dans cette direction (contours verticaux).
*/
cv::Mat derivative_x()
{
  // create and initialize and 1d array of integers
  cv::Mat kernel(3, 3, CV_64F, cv::Scalar(0));

  // build your kernel
  kernel.at<double>(1, 1) = -1.0;
  kernel.at<double>(1, 2) = 1.0;

  return kernel;
}

///////////////////////////////////////////////////////////////////////////////
/*
  apply_convolution() applique un noyau de convolution sur chaque pixel de l'image :
    - Elle parcourt chaque pixel (hors bords).
    - Pour chaque pixel (i,j) :
        - On parcourt un voisinage centré autour de (i,j)
        - Pour chaque voisin, on multiplie la valeur du pixel par le coefficient du noyau correspondant
        - On additionne tous ces produits : c’est la convolution
        - Le résultat est placé dans l’image de sortie à (i,j)
    - Le résultat est ajusté avec un offset (utile pour les dérivées).
    - Résultat limité entre 0 et 255 (valeurs valides d’un pixel 8 bits).
  Cela permet d’appliquer des effets comme flou, détection de contours, dérivées, etc.
*/
void apply_convolution(const cv::Mat &src, cv::Mat &dst, const cv::Mat &kernel, const unsigned char offset)
{
  dst = cv::Mat(src.size(), CV_8UC1, cv::Scalar(0));

  for (int i = kernel.rows / 2; i < dst.rows - (kernel.rows / 2); ++i)
    for (int j = kernel.cols / 2; j < dst.cols - (kernel.cols / 2); ++j)
    {
      double value = 0;
      for (int u = 0; u < kernel.rows; ++u)
        for (int v = 0; v < kernel.rows; ++v)
        {
          int pos_i = i - (kernel.rows / 2) + u;
          int pos_j = j - (kernel.cols / 2) + v;
          value += double(src.at<unsigned char>(pos_i, pos_j) * kernel.at<double>(u, v));
        }
      dst.at<unsigned char>(i, j) = std::clamp(value + offset, 0.0, 255.0);
    }
}

/*
  Question 5 :

    Ce noyau :
    [ 0  0  0 ]
    [ -1 0  1 ]
    [ 0  0  0 ]

    calcul bien une dérivée selon l'axe des x, car il mesure les variations horizontales entre les pixels gauche et droite
*/

/*
  Question 6 :

    La matrice de Sobel :
    [ -1  0  1 ]
    [ -2  0  2 ]
    [ -1  0  1 ]

    Cette matrice met plus de poids au centre (ligne du milieu a des plus gros coefficients)
*/

cv::Mat derivative_y()
{
  // create and initialize and 1d array of integers
  cv::Mat kernel(3, 3, CV_64F, cv::Scalar(0));

  // build your kernel
  kernel.at<double>(0, 1) = -1.0;
  kernel.at<double>(2, 1) = 1.0;

  return kernel;
}

cv::Mat laplacian()
{
  // create and initialize and 1d array of integers
  cv::Mat kernel(3, 3, CV_64F, cv::Scalar(0));

  // build your kernel
  kernel.at<double>(0, 1) = 1.0;

  kernel.at<double>(1, 0) = 1.0;
  kernel.at<double>(1, 1) = -4.0;
  kernel.at<double>(1, 2) = 1;

  kernel.at<double>(2, 1) = 1;

  return kernel;
}

cv::Mat contours() {
  cv::Mat kernel(3, 3, CV_64F, cv::Scalar(0));
  kernel.at<double>(0, 1) = -1.0;

  kernel.at<double>(1, 0) = -1.0;
  kernel.at<double>(1, 1) = 5.0;
  kernel.at<double>(1, 2) = -1.0;

  kernel.at<double>(2, 1) = -1.0;

  return kernel;
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

  // convert the image to grayscale
  cvtColor(image, image, cv::COLOR_BGR2GRAY);

  // display an image
  std::cout << "appuyer sur une touche voir l'image de base" << std::endl;
  cv::waitKey();
  cv::imshow("image", image);

  // do something here ... ////////
  cv::Mat filtered_image;

  // Question 4 : derivative x
  std::cout << "appuyer sur une touche pour voir la dérivée horizontale" << std::endl;
  cv::waitKey();
  apply_convolution(image, filtered_image, derivative_x(), 128);
  cv::imshow("image", filtered_image);

  // Question 7 : derivative y
  std::cout << "appuyer sur une touche pour voir la dérivée verticale" << std::endl;
  cv::waitKey();
  apply_convolution(image, filtered_image, derivative_y(), 128);
  cv::imshow("image", filtered_image);

  // Question 8 : Laplcacian
  apply_convolution(image, filtered_image, laplacian(), 128);
  std::cout << "appuyer sur une touche pour voir la dérivée seconde (Laplacian)" << std::endl;
  cv::imshow("image", filtered_image);
  cv::waitKey();

  apply_convolution(image, filtered_image, contours(), 0);
  std::cout << "appuyer sur une touche pour voir l'ajout des contours" << std::endl;
  cv::imshow("image", filtered_image);
  cv::waitKey();

  ////////////////////////////////

  std::cout << "appuyer sur une touche pour quitter le programme ..." << std::endl;
  cv::waitKey();
  // save images
  cv::imwrite("/home/M1/Traitement_Images/td-04/output/image.jpg", filtered_image);

  return 1;
}
