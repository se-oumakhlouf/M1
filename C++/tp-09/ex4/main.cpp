#include <set>
#include <tuple>
#include <iostream>

struct Point3D
{
    int x, y, z;

    bool operator<(const Point3D& other) const {
        return std::tie(x, y, z) < std::tie(other.x, other.y, other.z);
    }
};

class Point3DCompare {
    public:
    bool operator()(const Point3D& p1, const Point3D& p2) const {
        return std::tie(p1.x, p1.y, p1.z) < std::tie(p2.x, p2.y, p2.z);
    }
};

using SET = std::set<Point3D>;

void print_set(SET set) {
    std::cout << ("SET ->") << std::endl;
    for (const auto& [x, y, z]: set) {
        std::cout << "(" << x << ", " << y << ", " << z << ")" << std::endl;
    }
}

int main() {

    /* A.3
        std::set<Point3D> coords;
        Les objets contenus dans un set sont triées
        Notre struct Point3D n'a pas d'operator< utilisé par set pour trier / comparer
        L'insertion résulte donc en une erreur de compilation
    */
    SET coords;
    coords.emplace(Point3D{1, 2, 3});
    coords.insert(Point3D{111, 222, 333});
    print_set(coords);

    std::cout << "Suppression du premier élément : " << std::endl;
    coords.erase(coords.begin());
    print_set(coords);

    return 0;
}