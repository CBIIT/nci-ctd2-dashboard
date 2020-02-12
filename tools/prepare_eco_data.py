# download ECO terms data for CTD2 dashboard
import http.client
import shlex


class ECOTerm:
    def __init__(self):
        self.name = ""
        self.id = ""
        self.definition = ""
        self.synonyms = []

    def __str__(self):
        return self.name+"\t"+self.id+"\t"+self.definition+"\t"+"|".join(self.synonyms)


def main():
    conn = http.client.HTTPSConnection("raw.githubusercontent.com")
    conn.request(
        "GET", "/evidenceontology/evidenceontology/master/eco-basic.obo")
    r1 = conn.getresponse()
    print(r1.status, r1.reason)
    if r1.status != 200:
        print('download failed')
        return

    result_file = open('../admin/src/main/resources/ecoterms.txt', 'w')
    count = 0
    ecoterm = None
    for rawline in r1.readlines():
        count += 1
        line = rawline.decode('utf-8').strip()

        if count == 1:
            print('#', line, end='\t', file=result_file)
        if count == 3:
            print(line, file=result_file)
            print('name\tcode\tdefinition\tsynonyms', file=result_file)

        if line == '[Term]':
            ecoterm = ECOTerm()
        elif len(line) == 0 and ecoterm is not None:
            print(ecoterm, file=result_file)
            ecoterm = None
        elif ecoterm:
            try:
                x = shlex.split(line)
            except ValueError as e:
                print(e)
                print(line)
            if len(x) < 2:
                continue
            if x[0] == 'id:':
                ecoterm.id = x[1]
            elif x[0] == 'name:':
                ecoterm.name = line[6:]
            elif x[0] == 'def:':
                ecoterm.definition = x[1]
            elif x[0] == 'synonym:':
                ecoterm.synonyms.append( x[1] )
    conn.close()
    result_file.close()


if __name__ == "__main__":
    main()
