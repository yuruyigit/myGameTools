package game.data.conf.entity;

public class CardStarUps {
    private Integer id;

    private Integer roleId;

    private Integer star;

    private Integer type;

    private String steps;

    private Integer roleFragNum;

    private String itemsNum;

    private Integer roleNeedLevel;

    private String name;

    private String description;

    private String spell;

    private String attrs;

    private Integer display;

    private Integer spellLevel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps == null ? null : steps.trim();
    }

    public Integer getRoleFragNum() {
        return roleFragNum;
    }

    public void setRoleFragNum(Integer roleFragNum) {
        this.roleFragNum = roleFragNum;
    }

    public String getItemsNum() {
        return itemsNum;
    }

    public void setItemsNum(String itemsNum) {
        this.itemsNum = itemsNum == null ? null : itemsNum.trim();
    }

    public Integer getRoleNeedLevel() {
        return roleNeedLevel;
    }

    public void setRoleNeedLevel(Integer roleNeedLevel) {
        this.roleNeedLevel = roleNeedLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell == null ? null : spell.trim();
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs == null ? null : attrs.trim();
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public Integer getSpellLevel() {
        return spellLevel;
    }

    public void setSpellLevel(Integer spellLevel) {
        this.spellLevel = spellLevel;
    }
}