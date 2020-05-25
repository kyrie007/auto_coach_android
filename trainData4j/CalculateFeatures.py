
import xlrd
import numpy as np
import os

def calcData(data):
    maxAX = max(data[:, 4])
    maxAY = max(data[:, 3])
    minAX = min(data[:, 4])
    minAY = min(data[:, 3])
    maxAccX = max(abs(data[:, 4]))
    maxAccY = max(abs(data[:, 3]))
    datalist = data[:, 3].tolist()
    # print(datalist.index(max(datalist)))
    fraction = datalist.index(max(datalist))/len(datalist)


    rangeAX = maxAX - minAX
    rangeAY = maxAY - minAY
    startAY = data[0, 3]
    endAY = data[-1, 3]
    varAX = np.std(data[:, 4])
    varAY = np.std(data[:, 3])
    varOX = np.std(data[:, 7])
    varOY = np.std(data[:, 6])
    meanAX = np.mean(data[:, 4])
    meanAY = np.mean(data[:, 3])
    meanOX = np.mean(data[:, 6])
    maxOX = max(abs(data[:, 6]))
    maxOY = max(abs(data[:, 7]))
    maxOri = max(maxOX,maxOY)
    t = (data[-1, 1] - data[0, 1])/1000
    meanSP = np.mean(data[:, 2])
    differenceSP = data[-1, 2]-data[0, 2]
    # accelerate = differenceSP / t
    varSP = np.std(data[:,2])
    StartEndAccx = data[0,4]+data[-1,4]
    StartEndAccy = data[0,3]+data[-1,3]
    axis = 0
    if(data[0, -1]<6):
        axis = 0
    else:
        axis = 1

    return [rangeAX, rangeAY, varAX, varAY, meanAX, meanAY, meanOX, maxOri, maxAX, minAX, maxAccY,meanSP, StartEndAccx,StartEndAccy, t, axis, data[0, -1]] #99% 86%
    # return [rangeAX, rangeAY, varAX, varAY, meanAX, meanAY, meanOX, maxOri, maxAX, minAX, maxAccY,differenceSP,meanSP, StartEndAccx,StartEndAccy, t, axis, data[0, -1]] #99% 86%

    # return [rangeAX, rangeAY, varAX, varAY, varOX, varOY, meanAX, meanAY, meanOX, meanOY, maxOri, maxAX, maxAY, minAX, minAY, differenceSP, meanSP, varSP, StartEndAccx, StartEndAccy, t, data[0, -1]] #21  98% 83%
    # return [rangeAX, rangeAY, varAX, varAY, varOX, varOY, meanAX, meanAY, meanOX, meanOY, maxOri, maxAX, maxAY, minAX, minAY, differenceSP, meanSP, varSP, t, axis, data[0, -1]] #20 98% 83%
    # return [rangeAX, rangeAY, varAX, varAY, varOX, varOY, meanAX, meanAY, meanOX, meanOY, maxOri, maxAX, maxAY, minAX, minAY, differenceSP, meanSP, varSP, t, data[0, -1]] #19 98% 83%

# load raw data into workspace
def read_excel(file):
    data = xlrd.open_workbook(file)
    table = data.sheets()[0]

    start = 0  # 开始的行
    end = 31612  # 结束的行

    list_values = []
    for x in range(start, end):
        values = []
        row = table.row_values(x)
        # all data from excel
        for i in range(10):
            # print(value)
            values.append(float(row[i]))
        list_values.append(values)
    # print(list_values)
    datamatrix = np.array(list_values)
    print(datamatrix[0])
    datamatrix = datamatrix.astype(np.float64)
    # print(datamatrix)
    return datamatrix




# split data by event
def init(datamatrix):
    datamatrix = np.array(datamatrix)
    temp = 1.0
    vect = []  # break labeled 1
    speedV = []  # speed up labeled 2
    flag = 0
    resultMatrix = []
    for i in range(len(datamatrix)):
        if datamatrix[i, 0] == temp:
            flag += 1
        else:
            vect.append(calcData(datamatrix[i - flag:i, ]))
            resultMatrix.append([datamatrix[i-flag,1],datamatrix[i-1,1],datamatrix[i-1,-1]])
            temp = datamatrix[i, 0]
            flag = 1

    vect.append(calcData(datamatrix[len(datamatrix)+1 - flag:len(datamatrix)+1, ]))
    # file_w = Workbook()
    # table = file_w.add_sheet(u'Data', cell_overwrite_ok=True)  # 创建sheet
    # write_data(np.array(resultMatrix), table)
    # file_w.save('ForLDA.xls')

    # linear normalization
    max = np.max(vect, axis=0)
    min = np.min(vect, axis=0)
    print(max)
    print(min)
    # max = [0.7614, 0.6011, 0.2729, 0.2104, 11.510, 4.6303, 0.2529, 0.2861, 13.922, 1.6740, 31.65, 0.51791, 0.54475,
    #        0.1544,
    #        0.0674, 75.0, 94.7125, 29.1634,17.16]
    # min = [0.06909, 0.0079, 0.0206, 0.0020, 0.3709, 0.7642, -0.356, -0.277, -16.325, -2.0252, 2.27, -0.0867, -0.0405,
    #        -0.748,
    #        -0.589, -90.0, 2.67796, 0.40508, 1.848]
    for i in range(len(vect[0])-1):
        print(max[i])
        print(min[i])
        for j in range(len(vect)):
            # if(i==12):
            #     vect[j][i] = ((vect[j][i]-min[i])/(max[i]-min[i])*2)-1
            # else:
                vect[j][i] = (vect[j][i]-min[i])/(max[i]-min[i])


    return vect

def write_txt4j(data, file_name):
    if os.path.exists(file_name):
        os.remove(file_name)
    data = np.array(data)
    [h, l] = data.shape  # h为行数，l为列数
    file_write = open(file_name, 'a')
    for i in range(h):
        file_write.write(str(int(data[i][-1])))
        for j in range(l-1):
            file_write.write(" "+str(j+1)+":"+str(data[i][j]))
        file_write.write("\n")
    file_write.close()


def main():
    datamatrix = read_excel('label data.xlsx')
    vect = np.array(init(datamatrix))
    print(vect)
    # write_excel(vect, 'vect.xls')

    write_txt4j(vect, 'trainData4j.txt')



if __name__ == "__main__":
    main()