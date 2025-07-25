package cn.tealc.wutheringwavestool.model.plan;

/**
 * @ClassName
 * @Description TODO
 * @Author Nefeli
 * @Data 2025/7/25 13:53
 * @Version 1.0
 */
public class Plan {
    private String roleId;
    private String itemId;
    private int num;

    // Getters and setters
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
