import io
import json
import sys
import matplotlib
import scipy
import matplotlib.pyplot as plt

from PIL import Image
import numpy


# 解析获取数据
py_data = json.loads(sys.argv[1])
# py_data = json.loads(
#     '{\"data\":[0.001,0.002,0.003,0.004,0.005,0.0123,0.008981,0.001,0.002,0.003,0.004,0.005,0.0123,0.008981,0.001,0.002,0.003,0.004,0.005,0.0123,0.008981,0.011,0.012312,0.012342,0.01557,0.02888,0.0075686,0.0098]}')
data = py_data['data']


# 降噪算法处理
def main(data):
    y = scipy.signal.medfilt(volume=data, kernel_size=3)
    z = scipy.signal.medfilt(volume=data, kernel_size=15)

    plt.plot(data, 'k.')
    plt.plot(y, 'b.-')  # 在给定大小的邻域内取中值替代数据值，在邻域中没有元素的位置补0
    plt.plot(z, 'r.-')
    plt.legend(['original signal', 'length 3', 'length 15'])
    # plt.savefig('images/pythonImages/temp.png')

    return plt

res = main(data)


if isinstance(res, list):
    # 将算法分析后的数组数据转为规范数组返回
    result = dict()
    tmp = []
    for i in range(len(res)):
        tmp.append(res[i])
    result.__setitem__("data", tmp)
    print(json.dumps(result))
elif isinstance(res, matplotlib.__class__):
    # 将算法分析的图片转为图片二进制数据返回
    canvas = res.get_current_fig_manager().canvas
    canvas.draw()

    buffer = io.BytesIO()
    canvas.print_png(buffer)

    print(buffer.getvalue())

    buffer.close()
else:
    print()
