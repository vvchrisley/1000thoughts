# Contributing Guidelines

## Branching

- The repo is configured to prevent us from pushing to `main` directly. We want to keep it in a state anyone can branch from, so any commits with broken code will stay in your personal branches.
	- If you're working on two separate changes (for example, fixing a bug and implementing a new feature), create a new branch for each change.
	- Once the code in a branch is ready to be shared with the team, create a pull request so it can be reviewed and merged into `main`.
	- Once a pull request has been merged, it's safe to delete the corresponding branch.

## Code Reviews

- Pull requests require at least one approving review before merging.
- When reviewing, remember that the new code is being added to *your* project.
	- Don't be afraid to ask questions or say you'd do it differently -- it's a conversation, not a final judgement.

## References 

- https://github.com/agis/git-style-guide/blob/master/README.md
- https://github.com/openwrt/packages/blob/master/CONTRIBUTING.md
