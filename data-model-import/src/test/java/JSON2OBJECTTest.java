import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiziton.commons.vo.demo.DemoBean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JSON2OBJECTTest {

    public static void main(String[] args) {
        int a[] = {3,2,3,5,2};
        int singleNumberCompare = singleNumberCompare(a);
        System.out.println(singleNumberCompare);
        int singleNumberDistinct = singleNumberDistinct(a);
        System.out.println(singleNumberDistinct);
        int singleNumberDifference = singleNumberDifference(a);
        System.out.println(singleNumberDifference);
        int singleNumberXOR = singleNumberXOR(a);
        System.out.println(singleNumberXOR);
        Double aDouble = Double.valueOf("0.00");
        DemoBean bean = new DemoBean();
        bean.setAge(22);
        bean.setName("Hong");
        String[] aa = new String[2];
        aa[0] = "aa";
        aa[1] = "bb";
        Object c = bean;
        Class myclass = c.getClass();
        //bean.setTypes(aa);
        String json = JSON.toJSONString(bean);
        json += "&spilt:"+bean.getClass().getName();

        System.out.println(json);
        String[] jsons = json.split("&spilt:");
        JSONObject jsonObject = JSONObject.parseObject(jsons[0]);
        try{
            DemoBean stu = (DemoBean)JSONObject.toJavaObject(jsonObject, Class.forName(jsons[1]));
            System.out.println();
        }catch (Exception e){

        }
    }

    //比较法
    public static int singleNumberCompare(int[] nums) {
        Arrays.sort(nums);  // 排序数组
        for (int i = 0; i < nums.length - 1; i += 2) {
            // 找到不相等的一组，直接返回
            if (nums[i] != nums[i + 1]) {
                return nums[i];
            }
        }
        // 如果没有找到不相等的一组数据，直接返回数组的最后一个数字
        return nums[nums.length - 1];
    }

    //去重法
    public static int singleNumberDistinct(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (!set.add(nums[i])) { // add成功返回true，如果set中已有相同数字，则add方法会返回false
                set.remove(nums[i]); // 删除重复出现的数字
            }
        }
        return set.iterator().next();
    }

    //求差法
    public static int singleNumberDifference(int[] nums) {
        int num = 0;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            // 偶数下标位置 num += nums[i]，奇数下标位置 num -= nums[i]
            num = i % 2 == 0 ? num + nums[i] : num - nums[i];
            System.out.println( i % 2 + " " + num+" Difference " +nums[i]+" " +(num + nums[i])+" "+(num - nums[i]));
        }
        return num;
    }

    //异或法
    public static int singleNumberXOR(int[] nums) {
        int num = 0;
        for (int i = 0; i < nums.length; i++) {
            num = num ^ nums[i];
        }
        return num;
    }
}
