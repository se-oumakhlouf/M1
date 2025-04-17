#include <unordered_map>
#include <iostream>

struct Point2D
{
    int x, y;

    bool operator==(const Point2D &other) const
    {
        return x == other.x && y == other.y;
    }
};

namespace std
{
    template <>
    struct hash<Point2D>
    {
        size_t operator()(const Point2D &p) const
        {
            const auto a = p.x;
            const auto b = p.y;
            return (0.5 * (a + b) * (a + b + 1)) + b;
        }
    };
}

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

class Point2DEqual
{
public:
    bool operator()(const Point2D &p1, const Point2D &p2) const
    {
        auto compare = Point2DHash();
        return compare(p1) == compare(p2);
    }
};

using MAP = std::unordered_map<Point2D, Content>;

void print_map(MAP grid)
{
    std::cout << "\tMap\n";
    for (const auto &[point, content] : grid)
    {
        std::cout << "(" << point.x << ", " << point.y << ") => ";
        switch (content)
        {
        case Content::Empty:
            std::cout << "Empty";
            break;
        case Content::Red:
            std::cout << "Red";
            break;
        case Content::Yellow:
            std::cout << "Yellow";
            break;
        default:
            std::cout << "Unknown";
            break;
        }
        std::cout << std::endl;
    }
}

int main()
{
    // A.3
    // ne compile pas car il manque une fonction de hashage dans Point2D
    // std::unordered_map<Point2D, Content> grid;

    std::unordered_map<Point2D, Content> grid;
    grid.emplace(Point2D{10, 8}, Content::Red);
    grid.emplace(Point2D{1, 2}, Content::Empty);
    grid.emplace(Point2D{0, 0}, Content::Yellow);

    print_map(grid);

    std::cout << "On ajoute le point (100, 99)" << std::endl;
    grid[Point2D{100, 999}] = Content::Yellow;
    print_map(grid);

    std::cout << "On supprime le point (0, 0)" << std::endl;
    grid.erase(Point2D{0, 0});

    print_map(grid);
    return 0;
}