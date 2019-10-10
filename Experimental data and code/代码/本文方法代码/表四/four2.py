# -*- coding:utf-8 -*-
# Author:Zhou Yang





import timeit
from wikienzy import *





def timeuse(mss):
    """
    时间测试
    :param mss: 测试对象
    :return: 无
    """
    print(mss+":The use of time is={0:.6f}s\n".format(timeit.default_timer() - start))
    # start = timeit.default_timer()

if __name__ == '__main__':

    f = open("four2.txt", "w")

    word = ["Saccheri quadrilateral","Giordano Vitale","Omar Khayyám","Aristotle","Reductio ad absurdum","Hyperbolic geometry","Lambert quadrilateral"]
    for I in range(1):
        start = timeit.default_timer()
        A = word[I]
        A_title = summary(A)
        A_link = set(links(A))
        A_category = set(categories(A))
        A_category_category = set()
        A_category_subcategory = set()
        for i in A_category:
            i_category = set(supercats(i))
            A_category_category = A_category_category | i_category
            i_sub_category = set(subcats(i))
            A_category_subcategory = A_category_subcategory | i_sub_category
        A_link_category = set()
        A_link_title = set()
        A_link_links = list()
        s_ll_A = set()
        for i in A_link:
            i_category = set(categories(i))
            A_link_category = A_link_category | i_category
            A_link_title.add((i, summary(i)))
            i_link = set(links(i))
            A_link_links.append(i_link)
            s_ll_A = s_ll_A | i_link
        A_link_category_category = set()
        A_link_category_subcategory = set()
        for i in A_link_category:
            i_2category = set(supercats(i))
            A_link_category_category = A_link_category_category | i_2category
            i_subcategory = set(subcats(i))
            A_link_category_subcategory = A_link_category_subcategory | i_subcategory
        A_same = set(redirects(A))
        A_id = pageid(A)
        timeuse("初始化完成")
        A_link_num = (len(A_link) if len(A_link) > 0 else 1)
        for J in range(I + 1, len(word)):
            start = timeit.default_timer()
            B = word[J]
            B_link = set(links(B))
            B_category = set(categories(B))
            B_id = pageid(B)
            B_same = set(redirects(B))
            B_link_category = set()
            B_link_title = set()
            s_ll_B = set()
            B_link_num = (len(B_link) if len(B_link) > 0 else 1)
            H1 = True if B in A_link else False


            L1 = 0
            for i in A_link:
                if i in B_link:
                    L1 = L1 + 1
            L1 = L1 / A_link_num
            L2 = 0
            for i in B_link:
                if i in A_link:
                    L2 = L2 + 1
                i_category = set(categories(i))
                B_link_category = B_link_category | i_category
                i_link = set(links(i))
                s_ll_B = s_ll_B | i_link

            L2 = L2 / B_link_num
            H2 = (L1 - L2, L1, L2)

            L1 = 0
            for i in A_link:
                if i in s_ll_B:
                    L1 = L1 + 1
            L1 = L1 / A_link_num

            L2 = 0
            for i in B_link:
                if i in s_ll_A:
                    L2 = L2 + 1
                B_link_title.add((i, summary(i)))
            L2 = L2 / B_link_num
            H3 = (L1 - L2, L1, L2)
            if len(A_category_category) != 0:
                L1 = len(A_category_category & B_category) / len(A_category_category)
            else:
                L1 = 0
            if len(A_category_subcategory) != 0:
                L2 = len(A_category_subcategory & B_category) / len(A_category_subcategory)
            else:
                L2 = 0
            H4 = ((True if L1 - L2 < 0 else False), L1 - L2, L1, L2)
            L1 = len(A_link_category_category & B_category)
            L2 = len(A_link_category_subcategory & B_category)
            H5 = (L1 - L2, L1, L2)
            if len(A_link_category_category) != 0:
                L1 = len(A_link_category_category & B_link_category) / len(A_link_category_category)
            else:
                L1 = 0
            if len(A_link_category_subcategory) != 0:
                L2 = len(A_link_category_subcategory & B_link_category) / len(A_link_category_subcategory)
            else:
                L2 = 0
            H6 = (L1 - L2, L1, L2)
            H7 = False
            for i in B_same:
                if A_title.find(i) != -1:
                    H7 = True
                    break
            s_1, s_2 = 0, 0
            for i in A_link_title:
                for j in B_same:
                    if i[1].find(j) != -1:
                        s_1 = s_1 + 1
                        break
                for j in B_link:
                    if i[1].find(j) != -1:
                        s_2 = s_2 + 1
                        break
            s_1_1, s_2_2 = 0, 0
            for i in B_link_title:
                for j in A_same:
                    if i[1].find(j) != -1:
                        s_1_1 = s_1_1 + 1
                        break
                for j in A_link:
                    if i[1].find(j) != -1:
                        s_2_2 = s_2_2 + 1
                        break
            L1_1 = s_1 / A_link_num
            L1_2 = s_2 / A_link_num
            L2_1_1 = s_1_1 / B_link_num
            L2_2_2 = s_2_2 / B_link_num
            H8 = (L1_1 - L2_1_1, L1_1, L2_1_1)
            H9 = (L1_2 - L2_2_2, L1_2, L2_2_2)
            H10 = (A_id, B_id)
            print(A + ',' + B + ',' + str(H1) + ',' + str(H2[0]) + ',' + str(H2[1]) + ',' + str(H2[2]) + ','
                  + str(H3[0]) + ',' + str(H3[1]) + ',' + str(H3[2]) + ','
                  + str(H4[0]) + ',' + str(H4[1]) + ',' + str(H4[2]) + ',' + str(H4[3]) + ','
                  + str(H5[0]) + ',' + str(H5[1]) + ',' + str(H5[2]) + ','
                  + str(H6[0]) + ',' + str(H6[1]) + ',' + str(H6[2]) + ','
                  + str(H7) + ','
                  + str(H8[0]) + ',' + str(H8[1]) + ',' + str(H8[2]) + ','
                  + str(H9[0]) + ',' + str(H9[1]) + ',' + str(H9[2]) + ','
                  + str(H10[0]) + ',' + str(H10[1]))
            try:
                f.write(A + ',' + B + ',' + str(H1) + ',' + str(H2[0]) + ',' + str(H2[1]) + ',' + str(H2[2]) + ','
                        + str(H3[0]) + ',' + str(H3[1]) + ',' + str(H3[2]) + ','
                        + str(H4[0]) + ',' + str(H4[1]) + ',' + str(H4[2]) + ',' + str(H4[3]) + ','
                        + str(H5[0]) + ',' + str(H5[1]) + ',' + str(H5[2]) + ','
                        + str(H6[0]) + ',' + str(H6[1]) + ',' + str(H6[2]) + ','
                        + str(H7) + ','
                        + str(H8[0]) + ',' + str(H8[1]) + ',' + str(H8[2]) + ','
                        + str(H9[0]) + ',' + str(H9[1]) + ',' + str(H9[2]) + ','
                        + str(H10[0]) + ',' + str(H10[1]) + "\n")
            except UnicodeEncodeError as un:
                print(un)
                print("--------------------")
                f.write("----------------------" + "\n")
            else:
                pass
            timeuse("")
    f.close()






