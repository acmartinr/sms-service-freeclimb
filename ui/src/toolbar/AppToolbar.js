import React, {Component} from 'react';
import {isMobile} from 'react-device-detect';
import {Redirect} from 'react-router-dom';

import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import ArrowDropDown from '@material-ui/icons/ArrowDropDown';

import UserInfo from './UserInfo';
import './AppToolbar.css';

class AppToolbar extends Component {
  constructor(props) {
    super(props);

    this.state = {
      redirectToCampaigns: false,
      redirectToChat: false,
      anchorEl: undefined,
      open: false};
  }

  handleMenu(event) {
    this.setState({anchorEl: event.currentTarget, open: true});
  }

  handleClose() {
    this.setState({open: false, anchorEl: null});
  }

  redirectCampaigns() {
    this.setState({redirectToCampaigns: true});
  }

  redirectChat() {
    this.setState({redirectToChat: true});
  }

  render() {
    if (this.state.redirectToCampaigns) {
      return (<Redirect to='/campaigns'/>);
    }

    if (this.state.redirectToChat) {
      return (<Redirect to='/chat'/>);
    }

    return (
      <AppBar>
        <Toolbar>
          {/*this.props.drawer &&
            <IconButton
              edge="start"
              color="inherit"
              aria-label="Open drawer"
              onClick={(e) => this.props.handleDrawerOpen(e)}
              className='dashboard-menu-button {open && "menu-button-hidden"}'
            >
              <MenuIcon />
            </IconButton>
          */}

          <Typography
            component="h1" variant="h6" color="inherit" noWrap className="dashboard-title"
            onClick={(e) => isMobile && this.handleMenu(e)}>
            {this.props.title}
          </Typography>

          {isMobile && <React.Fragment>
            <ArrowDropDown
              className="pointer vertical-middle"
              onClick={(e) => this.handleMenu(e)} />

            <Menu
              id="navigation-appbar"
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              anchorEl={this.state.anchorEl}
              open={this.state.open}
              onClose={(e) => this.handleClose()}>
              {this.props.title === 'Chat' &&
                <MenuItem onClick={(e) => this.redirectCampaigns()}>Campaigns</MenuItem>
              }

              {this.props.title === 'Campaigns'  &&
                <MenuItem onClick={(e) => this.redirectChat()}>Chat</MenuItem>
              }
            </Menu>
          </React.Fragment>}

          <UserInfo applicationsMenuItem={this.props.applicationsMenuItem}/>
        </Toolbar>
      </AppBar>
    )
  }
}

export default AppToolbar;


