#include <unordered_map>

struct Point2D
{
    int x, y;
};

enum class Content
{
    Empty,
    Red,
    Yellow
};

class Point2DHash
{
public:
    std::size_t operator()(const Point2D &point) const
    {
        //  def cantor_pair(a, b):
        //      return (0.5 * (a + b) * (a + b + 1)) + b
        const auto a = point.x;
        const auto b = point.y;
        return (0.5 * (a + b) * (a + b + 1)) + b;
    }
};

class PointADEqual
{
public:
    std::size_t operator()(const Point2D &p1, const Point2D &p2) const
    {
        auto compare = Point2DHash();
        return compare(p1) == compare(p2);
    }
};

int main()
{
    // ne compile pas car il manque une fonction de hashage dans Point2D
    // std::unordered_map<Point2D, Content> grid;

    std::unordered_map<Point2D, Content, Point2DHash, PointADEqual> grid;
}