package pers.xanadu.enderdragon.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class AExpJ {
    public static <T> List<T> sample(final ConcurrentHashMap<T,Double> mp,int m){
        List<Pair<T,Double>> list = new ArrayList<>();
        mp.forEach((k,v)->list.add(new Pair<>(k,v)));
        return sample(list,m);
    }
    public static <T> List<T> sample(final List<Pair<T,Double>> samples, int m) {
        PriorityQueue<Pair<T,Double>> heap = new PriorityQueue<>(Comparator.comparingDouble(p -> p.second));
        double Xw = 0.0;
        double Tw = 0.0;
        double w_acc = 0.0;
        for (Pair<T,Double> sample : samples) {
            if (heap.size() < m) {
                double wi = sample.second;
                double ui = ThreadLocalRandom.current().nextDouble();
                double ki = Math.pow(ui, 1 / wi);
                heap.offer(new Pair<>(sample.first, ki));
                continue;
            }
            if (w_acc == 0) {
                if (heap.isEmpty()) break;
                Tw = heap.peek().second;
                double r = ThreadLocalRandom.current().nextDouble();
                Xw = Math.log(r) / Math.log(Tw);
            }
            double wi = sample.second;
            if (w_acc + wi < Xw) {
                w_acc += wi;
                continue;
            }
            else w_acc = 0;
            double tw = Math.pow(Tw, wi);
            double r2 = ThreadLocalRandom.current().nextDouble() * (1 - tw) + tw;
            double ki = Math.pow(r2, 1 / wi);
            heap.poll();
            heap.offer(new Pair<>(sample.first, ki));
        }
        List<T> res = new ArrayList<>();
        for (Pair<T,Double> pair : heap) {
            res.add(pair.first);
        }
        return res;
    }

//    public static void main(String[] args) {
//        List<Pair<String,Double>> samples = new ArrayList<>();
//        samples.add(new Pair<>("item1", 0.8));
//        samples.add(new Pair<>("item2", 0.4));
//        samples.add(new Pair<>("item3", 0.2));
//        Map<String,Integer> mp = new HashMap<>();
//        for(int i=0;i<1e5;i++){
//            List<String> result = sample(samples, 1);
//            mp.compute(result.get(0), (k, v)->v==null?1:v+1);
//        }
//        System.out.println(mp);
//    }
}
