package com.origami;

public class Main {

    public static void main(String[] args) {
	      if ( args.length < 1){
	          System.out.println("Please call with the size of map");
	          return;
        }
	      int size;
	      try {
	          size = Integer.parseInt(args[0]);
        } catch( NumberFormatException e){
	          System.out.println(args[0] + " is not a valid size");
	          return;
        }
	      ContentMap contentMap = new ContentMap(size);
	      contentMap.add("ID1", "Some Content");
				System.out.println("ID1="+contentMap.get("ID1"));
    }
}
