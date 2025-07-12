#!/usr/bin/env python3
"""
Generate launcher icons for Android app from logo.png
Creates icons for all required densities with ZeroTech logo on black background
"""

import os
from PIL import Image, ImageDraw

def create_launcher_icon(input_path, output_path, size):
    """Create a launcher icon with the logo centered on a black background"""
    try:
        # Open the logo image
        logo = Image.open(input_path)
        
        # Create a new image with black background
        icon = Image.new('RGBA', (size, size), (0, 0, 0, 255))
        
        # Calculate logo size (80% of icon size)
        logo_size = int(size * 0.8)
        
        # Resize logo maintaining aspect ratio
        logo.thumbnail((logo_size, logo_size), Image.Resampling.LANCZOS)
        
        # Calculate position to center the logo
        x = (size - logo.width) // 2
        y = (size - logo.height) // 2
        
        # Paste logo onto the black background
        icon.paste(logo, (x, y), logo if logo.mode == 'RGBA' else None)
        
        # Save the icon
        icon.save(output_path, 'PNG')
        print(f"Created {output_path} ({size}x{size})")
        
    except Exception as e:
        print(f"Error creating {output_path}: {e}")

def main():
    # Input logo path
    logo_path = "app/src/main/res/drawable/logo.png"
    
    # Check if logo exists
    if not os.path.exists(logo_path):
        print(f"Logo file not found: {logo_path}")
        return
    
    # Icon sizes for different densities
    icon_sizes = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192
    }
    
    # Create icons for each density
    for density, size in icon_sizes.items():
        # Create directory if it doesn't exist
        os.makedirs(f"app/src/main/res/{density}", exist_ok=True)
        
        # Create regular launcher icon
        create_launcher_icon(
            logo_path,
            f"app/src/main/res/{density}/ic_launcher.png",
            size
        )
        
        # Create round launcher icon
        create_launcher_icon(
            logo_path,
            f"app/src/main/res/{density}/ic_launcher_round.png",
            size
        )
    
    print("Launcher icon generation completed!")

if __name__ == "__main__":
    main() 
 
 
 
 
 