@PutMapping("/{groupId}/mambers/{memberId}")
    public ResponseEntity addMember(@PathVariable Integer groupId, @PathVariable Integer memberId) {
        Optional<Group> optional = groupService.findById(groupId);
        if(optional.isPresent()) {
            Group group = optional.get();
            Optional<Membership> optionalMembership = membershipService.findById(memberId);
            if(optionalMembership.isPresent()) {
                group.getMemberships().add(optionalMembership.get());
                GroupDTO dtoUpdate = entityToDto(groupService.createOrUpdate(group));
                return ResponseEntity.ok().body(dtoUpdate); // to show the updated group
            }
            return ResponseEntity.badRequest().body("This member does not exist!");
        }
        return ResponseEntity.badRequest().body("This group does not exist!");
    }
    
    // editMember() -> if(hasAccount()) can't edit | else ok --> In MembershipController
    
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity deleteMember(@PathVariable Integer groupId, @PathVariable Integer memberId) {
        Optional<Group> optional = groupService.findById(groupId);
        if(optional.isPresent()) {
            Group group = optional.get();
            Optional<Membership> optionalMembership = membershipService.findById(memberId);
            if(optionalMembership.isPresent()) {
                group.getMemberships().remove(optionalMembership.get());
                GroupDTO dtoUpdate = entityToDto(groupService.createOrUpdate(group));
                return ResponseEntity.ok().body(dtoUpdate); // to show the updated group
            }
            return ResponseEntity.badRequest().body("This member does not exist!");
        }
        return ResponseEntity.badRequest().body("This group does not exist!");
    }
    
