from PIL import Image
import os
import sys

def split_image_4x4(input_path, output_dir):
    img = Image.open(input_path)
    width, height = img.size

    cell_width = width // 4
    cell_height = height // 4

    os.makedirs(output_dir, exist_ok=True)

    for row in range(4):
        for col in range(4):
            left = col * cell_width
            upper = row * cell_height
            right = left + cell_width
            lower = upper + cell_height

            cell = img.crop((left, upper, right, lower))
            filename = f"cell_{row}_{col}.png"
            cell.save(os.path.join(output_dir, filename))

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python split_4x4.py <input_image> <output_dir>")
        sys.exit(1)

    split_image_4x4(sys.argv[1], sys.argv[2])
