import json
import sys
import matplotlib
import scipy
import numpy as np
import numpy
import matplotlib.pyplot as plt

def main(data) -> list:
    data = np.matrix(data) # type: np.matrix
    x, y = np.hsplit(data, [data.shape[1] - 1])
    a = np.linspace(1, 1, y.size)
    a.resize(y.shape[0], y.shape[1])
    ans = []
    if len(y) != 1:
        x = np.hstack([a, x])
        ans = (x.T * x).I * x.T * y
    else:
        x = np.vstack([a, x])
        ans = (x * x.T).I * x * y.T

    # a0 = round(ans[0, 0], 4)
    # a1 = round(ans[1, 0], 4)
    a0 = ans[0, 0]
    a1 = ans[1, 0]

    return [a0, a1]


if __name__ == "__main__":
    # arr_data = [[1,90],[2,120],[3,170],[4,210],[5,260]]
    # data = np.matrix(arr_data) # type: np.matrix

    # 传输数据要注意
    # 咱们这溶度电流的数据就按这个测试的结构
    # 那个1， 2， 3， 4就是电流，90，120……就是溶度
    py_data = json.loads(sys.argv[1])
    data = py_data.get('data')
    path = py_data.get('path')

    # 分割数组，默认最后一列是预测集

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



