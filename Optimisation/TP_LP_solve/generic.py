from curses.ascii import isalpha
import sys
import subprocess

def read_input_file(input_file):
    with open(input_file, 'r') as file:
        lines = file.readlines()

    m, n = map(int, lines[0].split())

    resource_limits = list(map(int, lines[1].split()))

    products = []
    for line in lines[2:]:
        parts = line.split()
        name = parts[0]
        requirements = list(map(int, parts[1:-1]))
        benefit = int(parts[-1])
        products.append((name, requirements, benefit))
    return m, n, resource_limits, products

def generate_lp_file(m, n, resource_limits, products, output_file):
    with open(output_file, 'w') as file:
        objective = " + ".join(f"{product[2]}{product[0]}" for product in products)
        file.write(f"max: {objective};\n")
        for i in range(m):
            constraint = " + ".join(f"{product[1][i]}{product[0]}" for product in products)
            file.write(f"{constraint} <= {resource_limits[i]};\n")
    
def add_int_constraint(m, products, output_file, resource_limits):
    with open(output_file, 'a') as file:
        constraint = ", ".join(f"{product[0]}" for product in products)
        file.write(f"int {constraint};")

def parse(result):
    lines = result.split("\n")
    opt_value = None
    variables = {}

    for line in lines:
        line = line.strip() 
        if line.startswith("Value of objective function:"):
            opt_value = float(line.split(":")[1].strip())
        elif len(line.split()) == 2:
            parts = line.split()
            var_name = parts[0]
            try:
                value = int(float(parts[1]))
                variables[var_name] = value
            except ValueError:
                pass
    if opt_value is not None:
        print(f"opt = {int(opt_value)}")
        for var, value in variables.items():
            print(f"{var} = {value}")
    else:
        print("Erreur : Impossible d'extraire la valeur optimale.")
        
                

def main():
    bool_int = False
    if len(sys.argv) < 3:
        print("Usage: python3 generic.py [-int] <input_file> <output_file>")
        sys.exit(1)
    if "-int" in sys.argv:
        bool_int = True
        input_file = sys.argv[2]
        output_file = sys.argv[3]

    else:
        input_file = sys.argv[1]
        output_file = sys.argv[2]

    m, n, resource_limits, products = read_input_file(input_file)

    generate_lp_file(m, n, resource_limits, products, output_file)
    if(bool_int):
        add_int_constraint(m, products, output_file, resource_limits)

    try:
        var = subprocess.run(["lp_solve", output_file], capture_output=True, text=True, check=True)
        parse(var.stdout)

    except FileNotFoundError:
        print("Erreur : lp_solve n'est pas installé ou n'est pas dans le PATH.")
        sys.exit(1)
    except subprocess.CalledProcessError as e:
        print("Erreur lors de l'exécution de lp_solve.")
        sys.exit(1)

if __name__ == "__main__":
    main()
