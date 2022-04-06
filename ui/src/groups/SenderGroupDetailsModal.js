import React, {Component} from 'react';

import './SenderGroupDetailsModal.css';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import TablePagination from '@material-ui/core/TablePagination';

import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';
import Checkbox from '@material-ui/core/Checkbox';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session'

class SenderGroupDetailsModal extends Component {
  constructor(props) {
    super(props);

    var state = {}
    if (props.selectedSenderGroup && props.selectedSenderGroup.id) {
      state = {
        name: props.selectedSenderGroup.name,
        id: props.selectedSenderGroup.id,
        title: 'Update Sender Group',
        nameError: false,
        nameUniqueError: false,
        submitButtonLabel: 'Update'};
    } else {
      state = {
        name: '', id: 0,
        title: 'New Sender Group',
        nameError: false,
        nameUniqueError: false,
        submitButtonLabel: 'Add'};
    }

    var common = {
      senders: [],
      selectedSenders: [],
      count: 0,
      rowsPerPage: 10,
      page: 0
    };

    for (var prop in common) {
      state[prop] = common[prop];
    }
    this.state = state;

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
    this.updateSenders = this.updateSenders.bind(this);
    this.updateSelectedSenders = this.updateSelectedSenders.bind(this);
  }

  componentDidMount() {
    this.updateSelectedSenders();
  }

  handleChangePage(event, value) {
    this.setState({page: value}, this.updateSenders);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value}, this.updateSenders);
  }

  updateSenders() {
    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage,
      search: this.state.search
    };

    CampaignsAPI.getSenders(request,
    response => {
      if (response.status === "OK") {
        const senders = response.data.senders;
        for (var i = 0; i < senders.length; i++) {
          senders[i].selected = false;
          for (var j = 0; j < this.state.selectedSenders.length; j++) {
            if (senders[i].id === this.state.selectedSenders[j].id) {
              senders[i].selected = true;
              break;
            }
          }
        }

        this.setState({
          senders: senders,
          count: response.data.count
        });
      }
    });
  }

  updateSelectedSenders() {
    const request = {
      id: this.props.selectedSenderGroup.id ? this.props.selectedSenderGroup.id : 0
    };

    CampaignsAPI.getSendersForGroup(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          selectedSenders: response.data
        });
        this.updateSenders();
      }
    });
  }

  addNewSenderGroup() {
    const senderGroup = {
      name: this.state.name,
      id: this.state.id,
      userId: Session.getUser().id,
      senders: this.state.selectedSenders
    }

    this.setState({nameUniqueError: false});

    if (senderGroup.name.length === 0) {
      this.setState({nameError: true});
    }

    if (senderGroup.name.length) {
      CampaignsAPI.createSenderGroup(senderGroup,
      response => {
        if (response.status === 'OK') {
          this.props.handleClose(true);
        } else {
          if (response.message.indexOf("name") !== -1) {
            this.setState({nameUniqueError: true});
          }
        }
      });
    }
  }

  onTextFieldChanged(event, field) {
    this.setState({
      nameError: false,
      [field]: event.target.value
    });
  }

  onCheckChanged(event, sender) {
    const value = event.target.checked;

    const senders = this.state.senders;
    for (var i = 0; i < senders.length; i++) {
      if (senders[i].id === sender.id) {
        senders[i].selected = value;
        break;
      }
    }

    if (value) {
      this.setState({
        selectedSenders: this.state.selectedSenders.concat(sender),
        'senders': senders
      });
    } else {
      var selectedSenders = this.state.selectedSenders.slice();
      selectedSenders.splice(this.state.selectedSenders.indexOf(sender), 1);

      this.setState({
        'selectedSenders': selectedSenders,
        'senders': senders
      });
    }
  };

  render() {
    return (
      <Dialog
        open
        onClose={this.props.handleClose}
        aria-labelledby="form-dialog-title"
        className="custom-group-dialog">

        <DialogTitle
          className="padding-bottom-0"
          id="form-dialog-title">{this.state.title}</DialogTitle>

        <DialogContent>
          <TextField
            autoFocus
            error={this.state.nameError}
            margin="dense"
            id="name"
            label="Sender Group Name"
            type="text"
            value={this.state.name}
            onChange={(e) => this.onTextFieldChanged(e, "name")}
            fullWidth
          />

          {this.state.nameUniqueError &&
            <Typography color="error" component="h5" variant="subtitle1">
              A sender group with entered name is already exists
            </Typography>
          }

          <Table className='margin-top-20' size="small">
            <TableHead>
              <TableRow>
                <TableCell className='checkbox-cell'/>
                <TableCell>Name</TableCell>
                <TableCell>Phone</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {this.state.senders.map(row => (
                <TableRow key={row.id}>
                  <TableCell>
                    <Checkbox
                      color="primary"
                      checked={row.selected}
                      onChange={(e) => this.onCheckChanged(e, row)}/>
                  </TableCell>
                  <TableCell>{row.name}</TableCell>
                  <TableCell>+{row.phone}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>

          <TablePagination
            rowsPerPageOptions={[10, 25, 50]}
            component="div"
            count={this.state.count}
            rowsPerPage={this.state.rowsPerPage}
            page={this.state.page}
            backIconButtonProps={{
              'aria-label': 'Previous Page',
            }}
            nextIconButtonProps={{
              'aria-label': 'Next Page',
            }}
            onChangePage={this.handleChangePage}
            onChangeRowsPerPage={this.handleChangeRowsPerPage}
          />

        </DialogContent>

        <DialogActions>
          <Button onClick={(e) => this.props.handleClose()} color="primary">
            Cancel
          </Button>
          <Button onClick={(e) => this.addNewSenderGroup()} color="primary">
            {this.state.submitButtonLabel}
          </Button>
        </DialogActions>
      </Dialog>
    )
  }
}

export default SenderGroupDetailsModal;
