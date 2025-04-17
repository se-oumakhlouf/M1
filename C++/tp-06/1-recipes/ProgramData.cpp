#include "ProgramData.hpp"

void ProgramData::add_material(std::string name)
{
    _materials.emplace_back(std::make_unique<Material>(name));
}

void ProgramData::get_materials(std::vector<const Material *> &materials) const
{
    for (const auto &material : _materials)
    {
        if (material != nullptr)
        {
            materials.emplace_back(material.get());
        }
    }
}

void ProgramData::register_recipe(std::vector<std::string> materials, std::vector<std::string> products)
{
    const auto &recipe = _recipes.emplace_back(_recipes.size() + 1, std::move(materials), std::move(products));
    std::cout << "Recipe " << recipe << "has been registered" << std::endl;
}

size_t ProgramData::indexOf(const std::string &name) const
{
    for (size_t i = 0; i < _materials.size(); i++)
    {
        const auto &material = _materials[i];
        if (material != nullptr && name == _materials[i]->get_name())
        {
            return i;
        }
    }
    return SIZE_MAX;
}

void ProgramData::collect_doable_recipes(std::vector<const Recipe *> &recipes) const
{
    for (const auto &recipe : _recipes)
    {
        bool is_doable = true;

        for (const auto &material : recipe.get_materials())
        {
            if (indexOf(material) == SIZE_MAX)
            {
                is_doable = false;
                break;
            }
        }

        if (is_doable == true)
        {
            recipes.emplace_back(&recipe);
        }
    }
}

ProductionResult ProgramData::produce(size_t recipe_id)
{
    ProductionResult result;

    if (_recipes.size() < recipe_id)
    {
        result.recipe = nullptr;
    }

    result.recipe = &_recipes[recipe_id - 1];

    for (const auto &material : result.recipe->get_materials())
    {
        if (indexOf(material) == SIZE_MAX)
        {
            result.missing_materials.emplace_back(material);
        }
    }

    if (!result.missing_materials.empty())
    {
        return result;
    }

    for (const auto &material : result.recipe->get_materials())
    {
        auto index = indexOf(material);
        _materials[index].reset();
    }

    for (const auto &product : result.recipe->get_products())
    {
        add_material(product);
    }

    return result;
}