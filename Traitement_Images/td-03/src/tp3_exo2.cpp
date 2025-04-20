#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>

/*
  Question 2 : Cette LUT représente une normalisation linéaire par soustraction du minimum
*/

///////////////////////////////////////////////////////////////////////////////
unsigned char clamp_uchar(const int value)
{
  return std::min(std::max(value, 0), 255);
}

///////////////////////////////////////////////////////////////////////////////
cv::Mat computeHistogramGS(const cv::Mat &image)
{
  // create and initialize and 1d array of integers
  cv::Mat histogram(256, 1, CV_32S, cv::Scalar(0));

  // compute the histogram
  for (int i = 0; i < image.rows; ++i)
    for (int j = 0; j < image.cols; ++j)
      histogram.at<int>(image.at<unsigned char>(i, j))++;

  return histogram;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
cv::Mat computeCumulatedHistogramGS(const cv::Mat &imageGS)
{

  cv::Mat histogram = computeHistogramGS(imageGS);
  cv::Mat cumulatedHistogram = histogram.clone();

  for (int i = 1; i < cumulatedHistogram.rows; i++)
  {
    cumulatedHistogram.at<int>(i) += cumulatedHistogram.at<int>(i - 1);
  }

  return cumulatedHistogram;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
cv::Mat histogramToImageGS(const cv::Mat &histogram)
{
  // create an image
  const unsigned int histoHeight = 100;
  cv::Mat histogramImage(histoHeight, 256, CV_8U, cv::Scalar(0));

  // find the max value of the histogram
  double minValue, maxValue;
  cv::minMaxLoc(histogram, &minValue, &maxValue);

  // write the histogram lines
  for (int j = 0; j < 256; ++j)
    cv::line(histogramImage, cv::Point(j, histoHeight), cv::Point(j, histoHeight - (histoHeight * histogram.at<int>(j)) / maxValue), cv::Scalar(255), 1);

  return histogramImage;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
cv::Mat generateLUTplop(const cv::Mat &image)
{

  // create a LUT
  cv::Mat myLUT(256, 1, CV_8U, cv::Scalar(0));

  // find min and max value
  double minValue, maxValue;
  cv::minMaxLoc(image, &minValue, &maxValue);

  // compute the normalized image LUT
  for (int i = 0; i < 256; i++)
    myLUT.at<unsigned char>(i) = clamp_uchar(i - minValue);

  return myLUT;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
cv::Mat LUTimageNormalize(const cv::Mat &image)
{

  // create a LUT
  cv::Mat myLUT(256, 1, CV_8U, cv::Scalar(0));

  // find min and max value
  double minValue, maxValue;
  cv::minMaxLoc(image, &minValue, &maxValue);

  // compute the normalized image LUT
  for (int i = 0; i < 255; i++)
  {
    myLUT.at<unsigned char>(i) = clamp_uchar(((i - minValue) * 255) / int(maxValue - minValue));
  }
  return myLUT;
}

cv::Mat generateLUThistogramEqualize(const cv::Mat &imageGS)
{

  cv::Mat cumulatedHistogram = computeCumulatedHistogramGS(imageGS);

  cv::Mat myLUT(256, 1, CV_8U, cv::Scalar(0));

  for (int i = 0; i < 255; i++)
  {
    myLUT.at<unsigned char>(i) = clamp_uchar((255 * cumulatedHistogram.at<int>(i)) / (imageGS.cols * imageGS.rows));
  }
  return myLUT;
}

//////////////////////////////////////////////////////////////////////////////////////////////////
cv::Mat apply_LUT(const cv::Mat &image, const cv::Mat &lut)
{

  // make a copy of image
  cv::Mat finalImage;
  image.copyTo(finalImage);

  // aplly the LUT
  for (int i = 0; i < finalImage.rows; i++)
    for (int j = 0; j < finalImage.cols; j++)
      finalImage.at<unsigned char>(i, j) = lut.at<unsigned char>(image.at<unsigned char>(i, j));

  return finalImage;
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
  std::cout << "appuyer sur une touche ..." << std::endl
            << std::endl;
  cv::imshow("image", image);
  cv::imshow("histogramme", histogramToImageGS(computeHistogramGS(image)));
  cv::waitKey();

  // normalize the image
  std::cout << "brigthness" << std::endl;
  cv::Mat normalizeImage = apply_LUT(image, generateLUTplop(image));
  cv::imshow("image", normalizeImage);
  cv::imshow("histogramme", histogramToImageGS(computeHistogramGS(normalizeImage)));
  std::cout << "appuyer sur une touche ..." << std::endl
            << std::endl;
  cv::waitKey();

  // normalize the image (image normalisée)
  std::cout << "image normalisation" << std::endl;
  normalizeImage = apply_LUT(image, LUTimageNormalize(image));
  cv::imshow("image", normalizeImage);
  cv::imshow("histogramme", histogramToImageGS(computeHistogramGS(normalizeImage)));
  std::cout << "appuyer sur une touche ..." << std::endl
            << std::endl;
  cv::waitKey();

  // display an image (image histogramme normalisé)
  std::cout << "histogram normalisation" << std::endl;
  normalizeImage = apply_LUT(image, generateLUThistogramEqualize(image));
  cv::imshow("image", normalizeImage);
  cv::imshow("histogramme", histogramToImageGS(computeHistogramGS(normalizeImage)));
  cv::imshow("histogramme cumule", histogramToImageGS(computeCumulatedHistogramGS(image)));
  std::cout << "appuyer sur une touche ..." << std::endl
            << std::endl;
  cv::waitKey();

  // display an image (image histogramme normalisé + blur)
  std::cout << "histogram normalisation + blur" << std::endl;
  normalizeImage = apply_LUT(image, generateLUThistogramEqualize(image));
  cv::GaussianBlur(normalizeImage, normalizeImage, cv::Size(15, 15), 5);
  cv::imshow("image", normalizeImage);
  cv::imshow("histogramme", histogramToImageGS(computeHistogramGS(normalizeImage)));
  cv::imshow("histogramme cumule", histogramToImageGS(computeCumulatedHistogramGS(image)));
  std::cout << "appuyer sur une touche ..." << std::endl
            << std::endl;
  cv::waitKey();

  return 1;

/*
  Résumé des effets des différentes LUTs et traitements sur l'image "théCafé.png"

  Étapes :
  --------

  1. Image originale :
    - Peu de contraste
    - Zone blanche vide en haut
    - Texte "Thé" en gris clair sur fond gris
    - Histogramme concentré sur une petite plage de niveaux

  2. generateLUTplop() – Décalage :
    - Décalage de l'histogramme vers le bas
    - Blanc devient gris clair, gris devient noir
    - Image assombrie, contraste inchangé

  3. LUTimageNormalize() – Normalisation linéaire :
    - Ré-étalement de l’histogramme sur [0, 255]
    - Le blanc devient noir, le gris clair devient gris
    - Augmente le contraste, mais perte d’informations visuelles
    - L’image devient très sombre

  4. generateLUThistogramEqualize() – Égalisation d'histogramme :
    - Utilisation de l’histogramme cumulatif
    - Répartition uniforme des niveaux de gris
    - Apparition de détails (ex : "CAFFE")
    - Peut provoquer des effets visuels indésirables (artefacts)

  5. Égalisation + flou gaussien :
    - Adoucit les transitions et les artefacts créés par l’égalisation
    - Rendu plus naturel, moins brutal
*/
}
