/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jab.docsearch.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class Table
 *
 * @version $Id: Table.java 100 2009-02-24 21:18:06Z henschel $
 */
public class Table {
    boolean showDebug = false;
    int rows = 1000;
    int cols = 20;


    public Table(int cols, int rows) {
        this.rows = rows;
        this.cols = cols;
        maxXItems = cols;
        maxYItems = rows;
        loadOK = true;
        tableSumStr = "Summary results.";
        colorStr = "blue";
        bgColorStr = "white";
        headerColorStr = "yellow";
        captionStr = "Caption";
        items = new String[rows][cols];
        col = 0;
        row = 0;
        colNum = 0;
        rowNum = 0;
    }


    public void add(String s, int i, int j) {
        items[j][i] = s;
    }


    public void show(int i, int j) {
        /*
         * for(int k = 0; k < j; k++) { for(int l = 0; l < i; l++); }
         */

    }


    public void save(String s, String s1, int i, int j) {
        try {
            File file = new File(s1, s);
            FileWriter filewriter = new FileWriter(file);
            PrintWriter printwriter = new PrintWriter(filewriter);
            for (int k = 0; k < i; k++) {
                for (int l = 0; l < j; l++) {
                    if (!items[k][l].equals("") && items[k][l] != null)
                        printwriter.println(items[k][l]);
                    if (l == j - 1 && k == 0)
                        printwriter.println("#@%$$&*&****#zzz_!");
                }

            }

            printwriter.close();
        } catch (IOException ioexception) {
            System.out.println("IOException" + ioexception.toString());
        }
    }


    public void clear() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++)
                items[i][j] = "";

        }

    }


    public void htmlLoad(String s, String s1) {
        // System.out.println("Importing HTML table.");
        try {
            // boolean flag = false;
            // boolean flag1 = false;
            File file;
            if (s1.equals(""))
                file = new File(s);
            else
                file = new File(s1, s);

            // begin new reader
            int curBint;
            FileReader inBufRdr = new FileReader(file);
            BufferedReader fullFileText = new BufferedReader(inBufRdr, 1);
            // StringBuffer inputSB=new StringBuffer();
            char c = ' ';
            // int curError=0;
            int curRow = 0;
            int curCol = 0;
            boolean beginTag = false;
            boolean inCell = false;
            String tagString = "";
            String compTag = "";
            String s5 = "";
            String cellContent = "";
            while ((curBint = fullFileText.read()) != -1) {
                c = (char) curBint;
                if (c == '<')
                    beginTag = true;
                if (c == '>') {
                    compTag = tagString.toLowerCase();
                    if (compTag.startsWith("<td") || compTag.startsWith("<th")) {
                        s5 = "";
                        inCell = true;
                    } else if (compTag.startsWith("</td") || compTag.startsWith("</th")) {
                        inCell = false;
                        s5 = s5 + c;
                        cellContent = removeTags(s5);
                        items[curRow][curCol] = cellContent;
                        s5 = "";
                        curCol++;
                        tagString = "";
                    } else if (compTag.startsWith("</tr")) {
                        curCol = 0;
                        curRow++;
                        inCell = false;
                    }
                    tagString = "";
                    beginTag = false;
                }
                if (beginTag)
                    tagString = tagString + c;
                if (inCell)
                    s5 = s5 + c;
            } // end while reading

            // System.out.println("Done.");
            loadOK = true;
            fullFileText.close();
        }
        catch (IOException ioe) {
            loadOK = false;
            System.out.println("Load Unsuccessful" + ioe.toString());
        }
        // finally {

        // } // end finally
    } // end for htmlLoad


    public void fpSave(String s, int i, int j) {
        try {
            File file = new File(s);
            FileWriter filewriter = new FileWriter(file);
            PrintWriter printwriter = new PrintWriter(filewriter);
            printwriter.println("<html><header><title>" + captionStr + "</title></header>");
            printwriter.println("<body bgcolor=" + colorStr + "><p>" + captionStr + "</p>");
            printwriter.println("<table bgcolor=#f0f0f0 border=1 bgcolor=" + bgColorStr + " summary=\"" + tableSumStr + "\">\n");
            printwriter.print("<tr><td>");
            boolean flag = false;
            boolean flag1 = false;
            int k = rowSize();
            boolean flag2 = false;
            for (int l = 0; l < i; l++) {
                for (int i1 = 0; i1 < j; i1++) {
                    if (items[l][i1].equals(""))
                        flag = true;
                    if (!flag) {
                        printwriter.print(items[l][i1] + "</td>");
                        if (i1 != k - 1)
                            if (l == 0) {
                                printwriter.print("<td>");
                            }
                            else {
                                printwriter.print("<td>");
                            }
                    }
                }

                if (items[l + 1][0] != null && items[l + 1][0].equals(""))
                    flag1 = true;
                if (items[l][0].equals(""))
                    flag2 = true;
                if (l < i - 1 && !flag1) {
                    printwriter.println("</tr>");
                    printwriter.print("<tr><td>");
                } else if (!flag2)
                    printwriter.println("</tr>");
                // else
                // if(flag2)
                // if(!flag1);
                flag = false;
                flag1 = false;
                flag2 = false;
            }

            printwriter.println("</table>");
            printwriter.println("</body>");
            printwriter.println("</html>");
            printwriter.close();
        } catch (IOException ioexception) {
            System.err.println("IOException");
        }
    }


    public void load(String s, String s1) {
        int i = 0;
        // boolean flag = false;
        int k = 0;
        try {
            File file = new File(s1, s);
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedreader = new BufferedReader(filereader, 1);
            OrderReader orderreader = new OrderReader();
            String s2;
            while ((s2 = bufferedreader.readLine()) != null)
                if (s2.equals("#@%$$&*&****#zzz_!")) {
                    i = k;
                } else {
                    orderreader.add(s2);
                    k++;
                }
            filereader.close();
            int j = k / i;
            int l = 0;
            for (int i1 = 0; i1 < j; i1++) {
                for (int j1 = 0; j1 < i; j1++) {
                    items[i1][j1] = orderreader.inItem(l);
                    l++;
                }

            }

        } catch (FileNotFoundException filenotfoundexception) {
            System.err.println("File " + s + " not found in " + s1);
        } catch (IOException ioexception) {
            System.err.println("IOException");
        }
    }


    public void fpImport(String s) {
        try {
            File file = new File(s);
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedreader = new BufferedReader(filereader, 1);
            // boolean flag = false;
            int i = 0;
            int j = 0;
            // boolean flag1 = false;
            // byte byte0 = 108;
            // byte byte1 = 107;
            boolean flag2 = false;
            int l = 0;
            OrderReader orderreader = new OrderReader();
            OrderReader orderreader1 = new OrderReader();
            OrderReader orderreader2 = new OrderReader();
            String s1;
            while ((s1 = bufferedreader.readLine()) != null) {
                int k = s1.length();
                if (k > 3) {
                    char c = s1.charAt(k - 1);
                    char c1 = s1.charAt(k - 2);
                    if (c == ' ' && c1 == ' ')
                        flag2 = true;
                    if (!flag2)
                        s1 = s1 + " ";
                    flag2 = false;
                }
                k = 0;
                orderreader.add(s1);
                l++;
            }
            int i1 = l / 4;
            for (int j1 = 0; j1 < i1; j1++)
                orderreader1.add(orderreader.inItem(j1));

            filereader.close();
            // boolean flag3 = false;
            // boolean flag4 = false;
            // boolean flag5 = false;
            int i2 = 0;
            boolean flag6 = false;
            for (int j2 = 0; j2 < i1; j2++)
                if (orderreader1.inItem(j2) != null && orderreader1.inItem(j2).length() > 3) {
                    String s2 = orderreader1.inItem(j2);
                    int k1 = s2.length();
                    int l1 = k1 - 3;
                    if (k1 > 3) {
                        char c2 = s2.charAt(l1);
                        if (c2 == ':') {
                            if (i2 > 0) {
                                for (int k2 = 0; k2 < i2; k2++)
                                    if (s2.equals(orderreader2.inItem(k2)))
                                        flag6 = true;

                            }
                            if (!flag6) {
                                orderreader2.add(orderreader1.inItem(j2));
                                i2++;
                            }
                            flag6 = false;
                        }
                    }
                }

            for (int l2 = 0; l2 < i2; l2++) {
                items[i][j] = orderreader2.inItem(l2);
                j++;
            }

            j = 0;
            i++;
            int i3 = 0;
            boolean flag7 = false;
            boolean flag8 = false;
            boolean flag9 = false;
            // boolean flag10 = false;
            for (; i3 < l; i3++) {
                int j3 = orderreader.inItem(i3).length();
                for (int k3 = 0; k3 < i2; k3++) {
                    if (orderreader.inItem(i3).equals(orderreader2.inItem(k3)))
                        flag7 = true;
                    if (orderreader.inItem(i3).equals("") || j3 >= 295)
                        flag9 = true;
                    if (i3 > 0 && orderreader.inItem(i3 - 1).equals(orderreader2.inItem(k3)))
                        flag8 = true;
                }

                if (flag7 && flag8) {
                    items[i][j] = "blank or too large to show";
                    if (++j >= i2) {
                        j = 0;
                        i++;
                    }
                } else if (!flag7 && !flag9 && flag8) {
                    items[i][j] = orderreader.inItem(i3);
                    if (++j >= i2) {
                        j = 0;
                        i++;
                    }
                } else if (flag9)
                    j = 0;
                flag7 = false;
                flag8 = false;
                flag9 = false;
            }

        } catch (FileNotFoundException filenotfoundexception) {
            System.err.println("File not found");
        } catch (IOException ioexception) {
            System.err.println("IOException");
        }
    }


    public String inItem(int i, int j) {
        String s = items[j][i];
        return s;
    }


    Double itemVal(int i, int j) {
        int k = 0;
        boolean flag = true;
        String s = items[i][j];
        k = s.length();
        for (int l = 0; l < k; l++) {
            char c = s.charAt(l);
            if (!((c == '0') | (c == '1') | (c == '2') | (c == '3') | (c == '4') | (c == '5') | (c == '6') | (c == '7') | (c == '8') | (c == '9') | (c == '.')))
                flag = false;
        }

        if (flag) {
            Double double1 = Double.valueOf(s);
            return double1;
        } else {
            String s1 = "0.00";
            Double double2 = Double.valueOf(s1);
            return double2;
        }
    }


    public int colSize() {
        int i = 0;
        // String s = "b";
        // boolean flag = false;
        boolean flag2 = false;
        try {
            for (int j = 0; j < rows; j++) {
                boolean flag1;
                if (items[i][0] != null) {
                    flag1 = false;
                    if (items[i][0].equals(""))
                        flag2 = true;
                } else {
                    flag1 = true;
                }
                if (!flag1 && !flag2)
                    i++;
                flag1 = false;
                flag2 = false;
            }

        } catch (Exception exception) {
            System.out.println("Error - null value in table : " + exception.toString());
        }
        return i;
    }


    public int rowSize() {
        int i = 0;
        // String s = "b";
        // boolean flag = false;
        boolean flag2 = false;
        for (int j = 0; j < cols; j++) {
            boolean flag1;
            if (items[0][i] != null) {
                flag1 = false;
                if (items[0][i].equals(""))
                    flag2 = true;
            } else {
                flag1 = true;
            }
            if (!flag1 && !flag2)
                i++;
            flag1 = false;
            flag2 = false;
        }

        return i;
    }


    Double rangeSum(int i, int j, int k, int l) {
        if (l == 0)
            l = 1;
        int i1 = 0;
        String s = "b";
        do {
            s = items[i1][0];
            i1++;
        } while (s != null);
        int j1 = --i1;
        // boolean flag = false;
        int l1 = 0;
        int i2 = k - i;
        int j2 = (i2 * j1 - (j1 - j) - (j1 - l)) + 2;
        int k2 = i;
        int l2 = j;
        Double double2 = Double.valueOf("0.00");
        boolean flag1 = true;
        if (i <= k || j > l && i < k || j < l && i == k)
            do {
                s = items[l2][k2];
                int k1 = s.length();
                if (++l2 >= j1) {
                    l2 = 0;
                    k2++;
                }
                for (int i3 = 0; i3 < k1; i3++) {
                    char c = s.charAt(i3);
                    if (!((c == '0') | (c == '1') | (c == '2') | (c == '3') | (c == '4') | (c == '5') | (c == '6') | (c == '7') | (c == '8') | (c == '9') | (c == '.')))
                        flag1 = false;
                }

                Double double1;
                if (flag1) {
                    double1 = Double.valueOf(s);
                } else {
                    s = "0.00";
                    double1 = Double.valueOf(s);
                }
                l1 += double1.intValue();
                flag1 = true;
            } while (k2 != k && l2 != l);
        s = "" + l1;
        double2 = Double.valueOf(s);
        return double2;
    }


    public void nhSort(int i, String s) {
        String s1 = "up";
        // String s2 = "down";
        // boolean flag = false;
        boolean flag1 = true;
        boolean flag2 = false;
        // String as[] = new String[13];
        String as1[] = new String[13];
        boolean flag3 = false;
        int l = 0;
        // String s7 = "b";
        int i1 = 0;
        boolean flag4 = false;
        for (int j1 = 0; j1 < rows; j1++) {
            if (items[0][i1] != null) {
                flag4 = false;
                if (items[0][i1].equals(""))
                    flag3 = true;
            } else {
                flag4 = true;
            }
            if (!flag4 && !flag3)
                i1++;
            flag4 = false;
            flag3 = false;
        }

        flag4 = false;
        flag3 = false;
        l = i1;
        int k1 = 0;
        i1 = 0;
        // s7 = "b";
        for (int l1 = 0; l1 < 10500; l1++) {
            boolean flag5;
            if (items[i1][0] != null) {
                flag5 = false;
                if (items[i1][0].equals(""))
                    flag3 = true;
            } else {
                flag5 = true;
            }
            if (!flag5 && !flag3)
                i1++;
            flag5 = false;
            flag3 = false;
        }

        k1 = i1;
        if (s.equals(s1)) {
            for (int i2 = k1 - 1; i2 > 0; i2--) {
                for (int k2 = k1 - 1; k2 > 1; k2--) {
                    String s3 = items[i2][i];
                    String s5 = items[k2][i];
                    String s8 = s3;
                    int j = s8.length();
                    for (int i3 = 0; i3 < j; i3++) {
                        char c = s8.charAt(i3);
                        if (!((c == '0') | (c == '1') | (c == '2') | (c == '3') | (c == '4') | (c == '5') | (c == '6') | (c == '7') | (c == '8') | (c == '9') | (c == '.')))
                            flag1 = false;
                    }

                    s8 = s5;
                    j = s8.length();
                    for (int k3 = 0; k3 < j; k3++) {
                        char c1 = s8.charAt(k3);
                        if (!((c1 == '0') | (c1 == '1') | (c1 == '2') | (c1 == '3') | (c1 == '4') | (c1 == '5') | (c1 == '6') | (c1 == '7') | (c1 == '8') | (c1 == '9') | (c1 == '.')))
                            flag1 = false;
                    }

                    if (flag1) {
                        // Double double3 = Double.valueOf(s5);
                        // Double double1 = Double.valueOf(s3);
                        int i4 = 0;
                        int i5 = 0;
                        try {
                            i4 = Integer.parseInt(s5);
                            i5 = Integer.parseInt(s3);
                        } catch (Exception exception) {
                            System.out.println("Table.nhSort() failed with Exception=" + exception.getMessage());
                        }
                        if (i4 > i5)
                            flag1 = false;
                    } else if (s5.compareTo(s3) < 0)
                        flag2 = true;
                    if (flag1 || flag2) {
                        for (int j4 = 0; j4 < l; j4++) {
                            as1[j4] = items[i2][j4];
                            items[i2][j4] = items[k2][j4];
                            items[k2][j4] = as1[j4];
                        }

                    }
                    flag1 = true;
                    flag2 = false;
                }

            }

        } else {
            for (int j2 = k1 - 1; j2 > 0; j2--) {
                for (int l2 = k1 - 1; l2 > 1; l2--) {
                    String s4 = items[j2][i];
                    String s6 = items[l2][i];
                    String s9 = s4;
                    int k = s9.length();
                    for (int j3 = 0; j3 < k; j3++) {
                        char c2 = s9.charAt(j3);
                        if (!((c2 == '0') | (c2 == '1') | (c2 == '2') | (c2 == '3') | (c2 == '4') | (c2 == '5') | (c2 == '6') | (c2 == '7') | (c2 == '8') | (c2 == '9') | (c2 == '.')))
                            flag1 = false;
                    }

                    s9 = s6;
                    k = s9.length();
                    for (int l3 = 0; l3 < k; l3++) {
                        char c3 = s9.charAt(l3);
                        if (!((c3 == '0') | (c3 == '1') | (c3 == '2') | (c3 == '3') | (c3 == '4') | (c3 == '5') | (c3 == '6') | (c3 == '7') | (c3 == '8') | (c3 == '9') | (c3 == '.')))
                            flag1 = false;
                    }

                    if (flag1) {
                        // Double double4 = Double.valueOf(s6);
                        // Double double2 = Double.valueOf(s4);
                        int k4 = 0;
                        int j5 = 0;
                        try {
                            k4 = Integer.parseInt(s6);
                            j5 = Integer.parseInt(s4);
                        } catch (Exception exception) {
                            System.out.println("Table.nhSort() failed with Exception=" + exception.getMessage());
                        }
                        if (k4 < j5)
                            flag1 = false;
                    } else if (s6.compareTo(s4) > 0)
                        flag2 = true;
                    if (flag1 || flag2) {
                        for (int l4 = 0; l4 < l; l4++) {
                            as1[l4] = items[j2][l4];
                            items[j2][l4] = items[l2][l4];
                            items[l2][l4] = as1[l4];
                        }

                    }
                    flag1 = true;
                    flag2 = false;
                }

            }

        }
    }


    public String removeTags(String s) {
        int i = s.length();
        boolean flag = false;
        String s1 = "";
        // byte byte0 = 32;
        for (int j = 0; j < i; j++) {
            char c = s.charAt(j);
            if (c == '<')
                flag = true;
            if (flag && c == '>')
                flag = false;
            if (!flag && c != '<' && c != '>')
                s1 = s1 + c;
        }

        return s1;
    }


    public int inTableRow(String s, int i, int j) {
        int k = -1;
        for (int l = 0; l < j; l++) {
            if (!s.equals(items[l][i]))
                continue;
            k = l;
            break;
        }

        return k;
    }


    public void setCaption(String toSet) {
        captionStr = toSet;
    }


    public String getCaption() {
        return captionStr;
    }


    public void htmlLoadString(String s) {
        try {
            // boolean flag = false;
            // boolean flag1 = false;
            String s1 = "";
            // boolean flag2 = false;
            // String s2 = "";
            // String s3 = "";
            // byte byte0 = 32;
            int i = 0;
            int j = 0;
            int k = s.length();
            int l = 0;
            // boolean flag3 = false;
            // boolean flag4 = flag3;
            while (l < k) {
                char c = s.charAt(l);
                if (s.charAt(l) == '\t') {
                    items[j][i] = s1.trim();
                    s1 = "";
                    i++;
                }
                if (s.charAt(l) == '\r' || s.charAt(l) == '\n') {
                    items[j][i] = s1.trim();
                    s1 = "";
                    i = 0;
                    j++;
                }
                l++;
                s1 = s1 + c;
            }
            // System.out.println("Done.");
            loadOK = true;
        } catch (Exception exception) {
            loadOK = false;
            System.out.println("Load Unsuccessful" + exception.toString());
        }
    }

    int maxXItems = cols;
    int maxYItems = rows;
    int col;
    int row;
    static final String keyStr = "#@%$$&*&****#zzz_!";
    String items[][];
    int Xpos;
    int Ypos;
    int colNum;
    int rowNum;
    public boolean loadOK;
    String tableSumStr;
    String colorStr;
    String bgColorStr;
    String headerColorStr;
    String captionStr;
}
