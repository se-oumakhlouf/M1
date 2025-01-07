coeffs = [2, 3, 3, 4, 4, 2, 1, 5, 5, 3, 1, 3, 2, 5, 5, 4, 3]

s = open('notes.txt', 'r').read().split('\n')

def parse(line,coeffs):
    res = {}
    aa = line.rstrip().split()
    res['prenom'] = aa[0]; aa = aa[1:]
    nn = aa[-17:]; assert all(n.isdigit() for n in nn)
    aa = aa[:-17]
    res['nom'] = ' '.join(aa)
    res['note']= round(10*sum(int(n)*c for n,c in zip(nn,coeffs))/(sum(coeffs)))/2
    return res

ee = [parse(e, coeffs) for e in s]

# correction
# def tri_nom(ee):
#     return sorted([e['nom'].upper()+' '+e['prenom']+' '+ str(e['note']) for e in ee])

def tri_nom(liste_etudiant):
    sorted_students = sorted(liste_etudiant, key=lambda etudiant: etudiant['nom'].upper())
    formated_lines = [ f"{etudiant['nom'].upper()} {etudiant['prenom']} {etudiant['note']}" for etudiant in sorted_students ]
    return formated_lines

# for l in tri_nom(ee):
#     print(l)

def stats(etudiants):
    size = len(etudiants)
    moyenne = sum(etudiant['note'] for etudiant in etudiants) / size
    etudiants_sorted_notes = sorted(etudiants, key=lambda etu: etu['note'])
    mediane = etudiants_sorted_notes[int(size / 2)]['note']
    admis = len([etudiant for etudiant in etudiants if etudiant['note'] >= 10])
    colles = len([etudiant for etudiant in etudiants if etudiant['note'] < 10])
    print(f"Moyenne = {moyenne} Mediane = {mediane}\n{admis} reçus, {colles} collés sur 100 étudiants")

# stats(ee)

def calcule_rangs(ee):
    ee = sorted(ee, key=lambda e: e['note'], reverse=True)
    rank = 0
    same_rank = 1
    note = -1
    for etudiant in ee :
        if etudiant['note'] != note :
            rank += same_rank
            note = etudiant['note']
            etudiant['rang'] = rank
            same_rank = 1
        else :
            same_rank += 1
            etudiant['rang'] = rank
    return ee

ee = calcule_rangs(ee)

# for etudiant in calcule_rangs(ee) :
#     print(f"{etudiant['rang']} {etudiant['nom'].upper()} {etudiant['prenom']} {etudiant['note']}")

def to_csv_file(etudiants):
    sorted_etudiants = sorted(etudiants, key=lambda etudiant: etudiant['nom'].upper())
    with open('examen_cobol.csv', 'w') as f:
        for etudiant in sorted_etudiants :
            f.write(f"{etudiant['nom'].upper()}\t{etudiant['prenom']}\t{etudiant['note']}\t{etudiant['rang']}\n")

to_csv_file(ee)

s.close()

