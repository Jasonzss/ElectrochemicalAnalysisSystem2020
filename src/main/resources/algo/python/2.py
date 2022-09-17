import json
import sys
import matplotlib
import scipy
import numpy
import matplotlib.pyplot as plt


# 算法内容
# 降噪算法处理
def main(data):
    y = scipy.signal.medfilt(volume=data, kernel_size=3)
    z = scipy.signal.medfilt(volume=data, kernel_size=15)

    plt.plot(data, 'k.')
    plt.plot(y, 'b.-')  # 在给定大小的邻域内取中值替代数据值，在邻域中没有元素的位置补0
    plt.plot(z, 'r.-')
    plt.legend(['original signal', 'length 3', 'length 15'])

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
        result.__setitem__("result", res)
    print(json.dumps(result))
