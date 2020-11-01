package com.hpe.kit;

import java.util.HashSet;
import java.util.Set;

public class WeightTest
{
    public static void main(String[] args) {
    	
    	WeightTest t=new WeightTest();
    	
        int number = 10; // total orders
        Double[] weight = new Double[]{30D,20D};
        
        //assign orders
        Integer[] count = new Integer[weight.length];
        
        for(int i= 0 ;i <number;i++)
        {
        	int w=t.getWeight(weight,count);
        	System.out.println(""+w);
        }
        
       for(Integer i :count)
       {
           System.err.println(i);
       }
}
    
    public  int getWeight(Double[] weight,Integer[] count){
             // current weight
              Double[] current = new Double[weight.length];
              for(int w=0;w<weight.length;w++)
              {
                  current[w] = weight[w]/(count[w]==null?1:count[w]);
              }
              int index = 0;
              Double currentMax = current[0];
              for(int d=1; d<current.length;d++)
              {
                  Boolean isTrue = true;
                  while (isTrue)
                  {
                      Set set = new HashSet();
                      for(Double c : current)
                      {
                          set.add(c);
                      }
                      if(set.size()==1)
                      {// it means equal
                          for(int e=0; e<current.length;e++)
                          {
                              current[e] = current[e]*Math.random();
                          }
                      }else
                          {
                              isTrue = false;
                          }
                  }
                  //compare all data to find next maxvalue
                  if(currentMax<current[d])
                  {
                      currentMax=current[d];
                      index =d;
                  }
              }
              count[index]=count[index]==null?1:count[index]+1;
    	  return index;
    }
}