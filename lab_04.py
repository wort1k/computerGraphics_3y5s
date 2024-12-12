import glfw
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *

  
angle_x = 0
angle_y = 0
last_x = 0
last_y = 0
is_dragging = False
zoom = -6.0  
projection_mode = '3D'
translation_x = 0.0
translation_y = 0.0
translation_z = 0.0


def init_gl():
    glClearColor(0.0, 0.0, 0.0, 1.0)
    glEnable(GL_DEPTH_TEST)
    print("OpenGL Initialized")

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluPerspective(45, 800 / 600, 0.1, 50.0)
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()


def draw_axes():
    """Draw XYZ axes and coordinate labels."""
    
    # Рисуем оси
    glBegin(GL_QUADS)
    
    # X-axis (red)
    glVertex3f(0.0, 0.0, 0.0)
    glVertex3f(10.0, 0.0, 0.0)
    glVertex3f(10.0, 0.01, 0.0)
    glVertex3f(0.0, 0.01, 0.0)
    
    glEnd()


    
scale_x = 1
scale_y = 1
vertices_K = [
    (0, -1.5, 0.0), (0.5, -1.5, 0.0), (0.5, 1.5, 0.0), (0, 1.5, 0.0),  # front face
    (0, -1.5, 0.5), (0, 1.5, 0.5), (0.5, 1.5, 0.5), (0.5, -1.5, 0.5),  # back face
    (0, -1.5, 0.0), (0, -1.5, 0.5), (0, 1.5, 0.5), (0, 1.5, 0.0),  # left face
    (0.5, -1.5, 0.0), (0.5, 1.5, 0.0), (0.5, 1.5, 0.5), (0.5, -1.5, 0.5),  # right face
    (0, 1.5, 0.0), (0, 1.5, 0.5), (0.5, 1.5, 0.5), (0.5, 1.5, 0.0),  # top face
    (0, -1.5, 0.0), (0.5, -1.5, 0.0), (0.5, -1.5, 0.5), (0, -1.5, 0.5),  # bottom face
    (0.5, 0, 0), (0.5, 0, 0.5), (1, 1.5, 0.5), (1, 1.5, 0),  # additional faces for K
    (0.5, 0.3, 0), (0.5, 0.3, 0.5), (0.9, 1.5, 0.5), (0.9, 1.5, 0),
    (1, 1.5, 0), (1, 1.5, 0.5), (0.9, 1.5, 0.5), (0.9, 1.5, 0), # top
    (0.5, 0.3, 0), (0.5, 0, 0), (1, 1.5, 0), (0.9, 1.5, 0), # mid
    (0.5, 0.3, 0.5), (0.5, 0, 0.5), (1, 1.5, 0.5), (0.9, 1.5, 0.5), #mid
    (0.5, 0, 0), (0.5, 0, 0.5), (1, -1.5, 0.5), (1, -1.5, 0),
    (0.5, -0.3, 0), (0.5, -0.3, 0.5), (0.9, -1.5, 0.5), (0.9, -1.5, 0),
    (1, -1.5, 0), (1, -1.5, 0.5), (0.9, -1.5, 0.5), (0.9, -1.5, 0),
    (0.5, -0.3, 0), (0.5, 0, 0), (1, -1.5, 0), (0.9, -1.5, 0),
    (0.5, -0.3, 0.5), (0.5, 0, 0.5), (1, -1.5, 0.5), (0.9, -1.5, 0.5)
]

def draw_K():
    global angle_x, angle_y, zoom, scale_x, scale_y, projection_mode, translation_x, translation_y, translation_z

    glColor3f(0, 0, 1)
    glPushMatrix()
    glTranslatef(translation_x, translation_y, zoom + translation_z)
    glRotatef(angle_x, 1, 0, 0)
    glRotatef(angle_y, 0, 1, 0)
    glScalef(scale_x, scale_y, 1.0)
    glBegin(GL_QUADS)
    for i in range(0, len(vertices_K), 4):
        for v in vertices_K[i:i+4]:
            # Проекция на плоскость в зависимости от выбранного режима
            if projection_mode == 'XY':
                glVertex3f(v[0], v[1], 0.0)  # Z-координата равна 0
            elif projection_mode == 'XZ':
                glVertex3f(v[0], 0.0, v[2])  # Y-координата равна 0
            elif projection_mode == 'YZ':
                glVertex3f(0.0, v[1], v[2])  # X-координата равна 0
            else:  # 3D-режим
                glVertex3f(*v)
    glEnd()

    glPopMatrix()


def mouse_button_callback(window, button, action, mods):
    global is_dragging, last_x, last_y
    if button == glfw.MOUSE_BUTTON_LEFT:
        if action == glfw.PRESS:
            is_dragging = True
            last_x, last_y = glfw.get_cursor_pos(window)
        elif action == glfw.RELEASE:
            is_dragging = False


def cursor_pos_callback(window, xpos, ypos):
    global angle_x, angle_y, last_x, last_y
    if is_dragging:
        dx = xpos - last_x
        dy = ypos - last_y

        angle_x += dy * 0.1
        angle_y += dx * 0.1

        last_x, last_y = xpos, ypos


def scroll_callback(window, xoffset, yoffset):
    global zoom
    zoom += yoffset * 0.5  
    zoom = max(-50.0, min(-1.0, zoom))  

def key_callback(window, key, scancode, action, mods):
    global scale_x, scale_y, vertices_K, projection_mode,  translation_x, translation_y, translation_z
    prev_x, prev_y = scale_x, scale_y
    if action == glfw.PRESS or action == glfw.REPEAT:
        if key == glfw.KEY_X:
            scale_x += 0.1
        elif key == glfw.KEY_Z:
            scale_x = max(0.1, scale_x - 0.1)
        elif key == glfw.KEY_Y:
            scale_y += 0.1
        elif key == glfw.KEY_H:
            scale_y = max(0.1, scale_y - 0.1)
        elif key == glfw.KEY_P:  
            if projection_mode == '3D':
                projection_mode = 'XY'
            elif projection_mode == 'XY':
                projection_mode = 'XZ'
            elif projection_mode == 'XZ':
                projection_mode = 'YZ'
            else:
                projection_mode = '3D'
        elif key == glfw.KEY_W: 
            translation_y += 0.1
        elif key == glfw.KEY_S: 
            translation_y -= 0.1
        elif key == glfw.KEY_A: 
            translation_x -= 0.1
        elif key == glfw.KEY_D:  
            translation_x += 0.1
        elif key == glfw.KEY_Q: 
            translation_z += 0.1
        elif key == glfw.KEY_E: 
            translation_z -= 0.1
        
    vertices_K = [(x * scale_x / prev_x, y * scale_y / prev_y, z) for x, y, z in vertices_K]

def main():
    if not glfw.init():
        print("GLFW initialization failed!")
        return

    window = glfw.create_window(800, 600, "OpenGL Parallelepiped - Mouse Controlled", None, None)
    if not window:
        print("Window creation failed!")
        glfw.terminate()
        return

    glfw.make_context_current(window)
    print("Window created, OpenGL context set")

    glfw.set_mouse_button_callback(window, mouse_button_callback)
    glfw.set_cursor_pos_callback(window, cursor_pos_callback)
    glfw.set_scroll_callback(window, scroll_callback)  # Обработчик для прокрутки мыши

    glfw.set_key_callback(window, key_callback)

    init_gl()

    while not glfw.window_should_close(window):
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

        draw_axes()
        draw_K()
        glfw.swap_buffers(window)
        glfw.poll_events()

    glfw.terminate()


if __name__ == "__main__":
    main()
