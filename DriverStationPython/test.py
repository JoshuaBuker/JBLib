import pygame
import time
from tabulate import tabulate
import os

def init_joystick():
    pygame.init()
    pygame.joystick.init()
    if pygame.joystick.get_count() < 1:
        raise Exception("No joystick found.")
    joystick = pygame.joystick.Joystick(0)
    joystick.init()
    return joystick

def get_joystick_values(joystick):
    pygame.event.pump()
    joystick_data = {
        'axes': {},
        'buttons': {},
        'hats': {}
    }
    
    # Get axis values
    for i in range(joystick.get_numaxes()):
        if i != 3:  # Exclude axis 3
            joystick_data['axes'][f'Axis {i}'] = joystick.get_axis(i)
    # Get button values
    for i in range(joystick.get_numbuttons()):
        joystick_data['buttons'][f'Button {i}'] = joystick.get_button(i)
    # Get POV (hat) values
    for i in range(joystick.get_numhats()):
        joystick_data['hats'][f'Hat {i}'] = joystick.get_hat(i)
    return joystick_data

def format_joystick_values(joystick_data):
    table = []
    headers = ["Type", "Index", "Value"]
    for axis, value in joystick_data['axes'].items():
        table.append(["Axis", axis, value])
    for button, value in joystick_data['buttons'].items():
        table.append(["Button", button, value])
    for hat, value in joystick_data['hats'].items():
        table.append(["Hat", hat, value])
    return tabulate(table, headers=headers, tablefmt="grid")

def main():
    joystick = init_joystick()
    try:
        while True:
            joystick_values = get_joystick_values(joystick)
            formatted_values = format_joystick_values(joystick_values)
            os.system('cls')
            print(formatted_values)
            time.sleep(0.1)
    except KeyboardInterrupt:
        print("Exiting...")
    finally:
        pygame.quit()

if __name__ == "__main__":
    main()
