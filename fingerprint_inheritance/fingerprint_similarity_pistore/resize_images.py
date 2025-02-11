from PIL import Image
import os

print('Enter the path of fingerprint data folders: ')
folder_path = str(input())

if not os.path.exists(folder_path + '_resized'):
    os.makedirs(folder_path + '_resized')

#name_fingerprint = open("fingerprint_images/Names.txt", 'r').readlines()
name_fingerprint = open(folder_path + "/Names.txt", 'r').readlines()
n_f = len(name_fingerprint)

for i in range(0,n_f):
    name_fingerprint[i] = name_fingerprint[i][:-1]

for n_name in range(0,n_f):


    input_image = Image.open(folder_path + "/" + name_fingerprint[n_name])

    # Ridimensiona l'immagine mantenendo l'aspect ratio
    width, height = input_image.size
    aspect_ratio = width / height
    new_width = 500
    new_height = int(new_width / aspect_ratio)
    resized_image = input_image.resize((new_width, new_height), resample=Image.LANCZOS)

    # Salva l'immagine ridimensionata
    resized_image.save(folder_path + '_resized/' + name_fingerprint[n_name], dpi=(500, 500))