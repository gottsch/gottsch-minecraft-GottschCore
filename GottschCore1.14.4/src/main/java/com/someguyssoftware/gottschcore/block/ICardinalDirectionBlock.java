
// TODO should there be a lower-level interface for all directional blocks? because whether it is horizontal only or not
// it is still a DirectionProperty value.
public interface ICardinalDirectionBlock {

  /**
   * get the DirectionProperty
   */
  public DirectionProperty getFacingProperty();
  
  public EnumFacing getFacingEnum();
}
