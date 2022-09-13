import json
import sys
import matplotlib
import scipy
import numpy
import matplotlib.pyplot as plt

# 算法内容
def main(data):
    x = data.get("expermental")
    y = data.get("predicted")
    equation = data.get("equation")
    para = data.get("param")
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

    return plt


if __name__ == "__main__":
    # 参数处理
    py_data = json.loads(sys.argv[1])
    data = py_data.get('data')
    path = py_data.get('path')

    # 算法执行 并 处理返回结果
    res = main(data)

    result = dict()

    if isinstance(res, numpy.ndarray):
        # 将算法分析后的数组数据转为规范数组返回
        tmp = []
        for i in range(len(res)):
            tmp.append(res[i])
        result.__setitem__("result", tmp)
    elif isinstance(res, matplotlib.__class__):
        plt.savefig(path)
        result.__setitem__("result", "the product image file path is : " + path)
    else:
        result.__setitem__("result", "error")

    print(json.dumps(result))
