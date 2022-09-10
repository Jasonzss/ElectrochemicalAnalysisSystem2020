import io
import json
import sys
import matplotlib.pyplot as plt
from matplotlib.backends.backend_qt import FigureCanvasQT


def print_img(x: list, y: list, equation: str, para: dict):
    # 获得方程的参数
    fx = ''
    for item in equation.split("="):
        if "x" in item:
            fx = item.strip()
    a = 0
    b = 0
    for e in fx.split("+"):
        if "x" in e:
            a = e[:e.index("x")].strip()
            if len(a) == 0:
                a = 1
            else:
                a = float(a)
        else:
            b = float(e.strip())

    # 画函数图需要的两个点，这里选择第一个点和最后一个点（根据x的大小划分第一和最后）
    x_head = min(x)
    y_head = a * x_head + b
    x_tail = max(x)
    y_tail = a * x_tail + b

    plt.xlabel("Expermental/μM")
    plt.ylabel("Predicted/μM")

    line = plt.plot([x_head, x_tail], [y_head, y_tail], "r", x, y, "k.")
    # 标记函数方程
    plt.legend(line, [equation])
    # plt.text(x_head, max(y), "—", weight = "heavy", color="r")
    # plt.text(x_head, max(y), "    " + equation)
    # 标记变量
    para_str = ""
    key_text_mapper = {
        "rc2": "R$^2_c$",
        "rmsec": "RMSEC",
        "maec": "MAEC",
        "rp2": "R$^2_p$",
        "rmsep": "RMSEP",
        "maep": "MAEP",
        "rpd": "RPD"
    }
    for key in para.keys():
        value = key_text_mapper.get(key, "传进来的参数有误，不属于实验报告实体类的属性名！")
        para_str += "\n" + str(value) + " = " + str(para.get(key))

    plt.text(x_head, max(y), para_str, verticalalignment="top")

    fig_manager = plt.get_current_fig_manager() # type: FigureCanvasQT
    canvas = fig_manager.canvas
    buffer = io.BytesIO()
    canvas.print_png(buffer)
    print(buffer.getvalue())
    
    buffer.close()
    # plt.show()



if __name__ == "__main__":
    data = json.loads(sys.argv[1])
#     data = eval('{"expermental":[1, 2, 3, 4, 5], "predicted":[90, 120, 170, 210, 260], "equation": "y=43x+41", "param": {"rc2":0.1111, "maec":0.2222}}')

    x = data.get("expermental")
    y = data.get("predicted")
    equation = data.get("equation")
    para = data.get("param")

    print_img(x, y, equation, para)
